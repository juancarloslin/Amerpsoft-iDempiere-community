/******************************************************************************
 * Copyright (C) 2015 Luis Amesty                                             *
 * Copyright (C) 2015 AMERP Consulting                                        *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 ******************************************************************************/
package org.amerp.amnmodel;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

import org.compiere.model.*;
import org.compiere.util.*;


/**
 * @author luisamesty
 *
 */
public class MAMN_NonBusinessDay extends X_C_NonBusinessDay {

	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

	/**
	 * Logger
	 */
    
	static CLogger	log = CLogger.getCLogger (MAMN_NonBusinessDay.class);
	
	/**
	 * @param p_ctx
	 * @param C_NonBusinessDay_ID
	 * @param p_trxName
	 */
    public MAMN_NonBusinessDay(Properties p_ctx, int C_NonBusinessDay_ID, String p_trxName) {
	    super(p_ctx, C_NonBusinessDay_ID, p_trxName);
	    // TODO Auto-generated constructor stub
    }

	/**
	 * @param p_ctx
	 * @param p_rs
	 * @param p_trxName
	 */
    public MAMN_NonBusinessDay(Properties p_ctx, ResultSet p_rs, String p_trxName) {
	    super(p_ctx, p_rs, p_trxName);
	    // TODO Auto-generated constructor stub
    }

    /*********************************************************************************
	 *	Get SQL Get sqlGetHolliDaysBetween 
	 *  Description: Return absolute Holidays defined on Table c_nonbusinessday
	 *  				Does not takes care if Weekend (NonLaborDay)
	 *  @param  p_StartDate    
	 *  @param  p_EndDate The Payroll End Date date 
	 * 	@param	p_AD_Client_ID client
	 * 	@param  p_AD_Org_ID	organization
	 *  @return BigDecimal number of Holidays between 2 dates 
	 *********************************************************************************/
	public static BigDecimal sqlGetHolliDaysBetween (Timestamp p_StartDate, 
			 Timestamp p_EndDate, int p_AD_Client_ID, int p_AD_Org_ID)
	{

		MCalendar mcalendar = MCalendar.getDefault(Env.getCtx(), p_AD_Client_ID);
		int C_Calendar_ID = mcalendar.getC_Calendar_ID();
		//	Get Rate
		String sql = "SELECT " + 
				"count(*) as hollidays " + 
				"FROM c_nonbusinessday " + 
				"where  " + 
				"date1 BETWEEN ? AND ? " + 
				"AND c_calendar_id = ? " + 
				"AND IsActive ='Y' " +
				"AND ad_client_id IN (0,?) " + 
				"AND ad_org_id IN (0,?) "  ;
		BigDecimal retValue = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setTimestamp(1, p_StartDate);
			pstmt.setTimestamp(2, p_EndDate);
			pstmt.setInt(3, C_Calendar_ID);
			pstmt.setInt(4, p_AD_Client_ID);
			pstmt.setInt(5, p_AD_Org_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
				retValue = rs.getBigDecimal(1);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "sqlGetHolliDaysBetween", e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}		
		if (retValue == null) {
			if (log.isLoggable(Level.INFO)) log.info ("sqlGetHolliDaysBetween - not found - " 
			  + ", C_Calendar_ID=" + C_Calendar_ID
			  + ", StartDate=" + p_StartDate 
			  + ", EndDate=" + p_EndDate 
			  + ", Client=" + p_AD_Client_ID 
			  + ", Org=" + p_AD_Org_ID);
			retValue=BigDecimal.valueOf(0.00);
		}
		return retValue;
	}	//	sqlGetNonBusinessDay

	/*************************************************************************
	 *  sqlGetBusinessDaysBetween
	 *  Description: Return Absolute Business Days 
	 *  			Non Weekend days  less Days defined on Table c_nonbusinessday
	 *  			and included on non weekend days
	 *  @param Timestamp startDate,
	 *  @param Timestamp endDate, 
	 *  @param int p_AD_Client_ID, 
	 *  @param int p_AD_Org_ID
	 * Returns BigDecimal number of business days between 2 dates 
	 * ***********************************************************************/
	public static BigDecimal sqlGetBusinessDaysBetween(Timestamp p_StartDate,
			Timestamp p_EndDate, int p_AD_Client_ID, int p_AD_Org_ID)
	{
		MCalendar mcalendar = MCalendar.getDefault(Env.getCtx(), p_AD_Client_ID);
		BigDecimal retValue = null;
		BigDecimal NonWeekendDays = BigDecimal.valueOf(0);
		BigDecimal HolidaysonNonWeekendDays = BigDecimal.valueOf(0);
		// Get NonWeekendDays
		NonWeekendDays = sqlGetNonWeekEndDaysBetween ( p_StartDate, 
				  p_EndDate,  p_AD_Client_ID, null);
		int C_Calendar_ID = mcalendar.getC_Calendar_ID();
		//	Get Rate
		String sql = "SELECT " +
				"count(extract(dow from date1::timestamp) ) as BDAY "+
				"FROM c_nonbusinessday " +
				"WHERE date1 BETWEEN ? AND ? " +
				"AND ad_client_id IN (0,?) "+
				"AND ad_org_id IN (0,?) " +
				"AND IsActive ='Y' " +
//				"AND extract(dow from date1::timestamp) IN(1,2,3,4,5,6) "  
				"AND extract(dow from date1::timestamp) IN(1,2,3,4,5) "  // MONDAYS THRU FIDAYS (Sundays/Saturdays OFF)
				 ;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setTimestamp(1, p_StartDate);
			pstmt.setTimestamp(2, p_EndDate);
			pstmt.setInt(3, p_AD_Client_ID);
			pstmt.setInt(4, p_AD_Org_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
				HolidaysonNonWeekendDays = rs.getBigDecimal(1);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "sqlGetBusinessDaysBetween", e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}		
		if (HolidaysonNonWeekendDays == null) {
			if (log.isLoggable(Level.INFO)) log.info ("sqlGetBusinessDaysBetween - not found - " 
			  + ", C_Calendar_ID=" + C_Calendar_ID
			  + ", PayrollStartDate=" + p_StartDate 
			  + ", PayrollEndDate=" + p_EndDate 
			  + ", Client=" + p_AD_Client_ID 
			  + ", Org=" + p_AD_Org_ID);
			HolidaysonNonWeekendDays=BigDecimal.ZERO;
		}
		retValue= NonWeekendDays.subtract(HolidaysonNonWeekendDays);
		//log.warning("HolidaysonNonWeekendDays:"+HolidaysonNonWeekendDays+"   NonWeekendDays:"+NonWeekendDays);
		// Verify if negative 
		if (retValue.compareTo(BigDecimal.valueOf(0)) > 0)			
			return retValue;
		else
			return BigDecimal.valueOf(0);
	}

	
    /*********************************************************************************
	 *	Get SQL sqlGetNonBusinessDayBetween 
	 *  Description: Return absolute Non Business Days 
	 *  				Weekend days  
	 *  				plus Values defined on Table c_nonbusinessday
	 *  				on  weekend days
	 *  @param  p_StartDate    
	 *  @param  p_EndDate The Payroll End Date date 
	 * 	@param	p_AD_Client_ID client
	 * 	@param  p_AD_Org_ID	organization
	 *  @return BigDecimal number of non business days between 2 dates 
	 *********************************************************************************/
	public static BigDecimal sqlGetNonBusinessDayBetween (Timestamp p_StartDate, 
			 Timestamp p_EndDate, int p_AD_Client_ID, int p_AD_Org_ID)
	{
		MCalendar mcalendar = MCalendar.getDefault(Env.getCtx(), p_AD_Client_ID);
		BigDecimal retValue = null;
		BigDecimal WeekendDays = BigDecimal.valueOf(0);
		BigDecimal HolidaysonNonWeekendDays = BigDecimal.valueOf(0);
		// Get NonWeekendDays
		WeekendDays = sqlGetWeekEndDaysBetween ( p_StartDate, 
				  p_EndDate,  p_AD_Client_ID, null);
		int C_Calendar_ID = mcalendar.getC_Calendar_ID();
		//	Get Rate
		String sql = "SELECT " +
				"count(extract(dow from date1::timestamp) ) as BDAY " +
				"FROM c_nonbusinessday " +
				"WHERE date1 BETWEEN ? AND ? " +
				"AND ad_client_id IN (0,?) " +
				"AND ad_org_id IN (0,?) " +
				"AND IsActive ='Y' " +
	//			"AND extract(dow from date1::timestamp) IN(1,2,3,4,5,6)"
				"AND extract(dow from date1::timestamp) IN(1,2,3,4,5) "  // MONDAYS THRU FIDAYS (Sundays/Saturdays OFF)
				 ;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setTimestamp(1, p_StartDate);
			pstmt.setTimestamp(2, p_EndDate);
			pstmt.setInt(3, p_AD_Client_ID);
			pstmt.setInt(4, p_AD_Org_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
				HolidaysonNonWeekendDays = rs.getBigDecimal(1);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "sqlGetNonBusinessDay", e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}		
		if (HolidaysonNonWeekendDays == null) {
			if (log.isLoggable(Level.INFO)) log.info ("sqlGetNonBusinessDayBetween - not found - " 
			  + ", C_Calendar_ID=" + C_Calendar_ID
			  + ", PayrollStartDate=" + p_StartDate 
			  + ", PayrollEndDate=" + p_EndDate 
			  + ", Client=" + p_AD_Client_ID 
			  + ", Org=" + p_AD_Org_ID);
			HolidaysonNonWeekendDays=BigDecimal.valueOf(0.00);
		}
		//log.warning("HolidaysonNonWeekendDays:"+HolidaysonNonWeekendDays+"   WeekendDays:"+WeekendDays);
		retValue= WeekendDays.add(HolidaysonNonWeekendDays);	
		return retValue;
	}	//	sqlGetNonBusinessDay
	
	/*************************************************************************
	 *  sqlGetWeekEndDaysBetween
	 *  Description: Return absolute WeekEnd Days (Saturdays and Sundays)
	 *  				Does not takes care of Non BusinessDays
	 *  @param Timestamp startDate,
	 *  @param Timestamp endDate, 
	 *  @param int p_AD_Client_ID, 
	 *  @param String trxName
	 * Returns BigDecimal number of non business days between 2 dates 
	 * ***********************************************************************/
	public static BigDecimal sqlGetWeekEndDaysBetween(Timestamp startDate, Timestamp endDate, int p_AD_Client_ID, String trxName)
	{
		double retValue = 0;

		if (startDate.equals(endDate))
			return BigDecimal.valueOf(0.00);

		boolean negative = false;
		if (endDate.before(startDate)) {
			negative = true;
			Timestamp temp = startDate;
			startDate = endDate;
			endDate = temp;
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(endDate);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);

		while (cal.before(calEnd) || cal.equals(calEnd)) {
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				retValue++;
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		if (negative)
			retValue = retValue * -1;
		return BigDecimal.valueOf(retValue);
	}
	
	/*************************************************************************
	 *  sqlGetNonWeekEndDaysBetween
	 *  Description: Return absolute Non WeekEnd Days (Mondays thru Fridays)
	 *  				Does not takes care of Non BusinessDays
	 *  @param Timestamp startDate,
	 *  @param Timestamp endDate, 
	 *  @param int p_AD_Client_ID, 
	 *  @param String trxName
	 * Returns BigDecimal number of non business days between 2 dates 
	 * ***********************************************************************/
	public static BigDecimal sqlGetNonWeekEndDaysBetween(Timestamp startDate, 
			Timestamp endDate, int p_AD_Client_ID, String trxName)
	{
		double retValue = 0;

		if (startDate.equals(endDate))
			return BigDecimal.valueOf(0.00);

		boolean negative = false;
		if (endDate.before(startDate)) {
			negative = true;
			Timestamp temp = startDate;
			startDate = endDate;
			endDate = temp;
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(endDate);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);

		while (cal.before(calEnd) || cal.equals(calEnd)) {
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				retValue++;
				//log.warning(".....................sqlGetNonWeekEndDaysBetween...................................");
				//log.warning("retValue: ="+retValue+"  getTimeInMillis()="+cal.getTimeInMillis());	
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		if (negative)
			retValue = retValue * -1;
		return BigDecimal.valueOf(retValue);
	}

	

	/******************************************************************************
	 * 	Calculate the number of days between start and end.
	 * 	@param start start date
	 * 	@param end end date
	 * 	@return BigDecimal number of days (0 = same)
	 ******************************************************************************/
	static public BigDecimal getDaysBetween (Timestamp start, Timestamp end)
	{
		boolean negative = false;
		double difftmp=0;
		if (end.before(start))
		{
			negative = true;
			Timestamp temp = start;
			start = end;
			end = temp;
		}
		//
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(start);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(end);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);
		//	log.warning("Start=" + start + ", End=" + end + ", dayStart=" + cal.get(Calendar.DAY_OF_YEAR) + ", dayEnd=" + calEnd.get(Calendar.DAY_OF_YEAR));
		//	in same year
		if (cal.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR))
		{
			if (negative)
				difftmp = (calEnd.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR)) * -1;
			difftmp = calEnd.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
		} else {
			//	not very efficient, but correct
			while (calEnd.after(cal))
			{
				cal.add (Calendar.DAY_OF_YEAR, 1);
				difftmp++;
			}
			if (negative)
				difftmp = difftmp * -1;
		}	//	getDaysBetween
		return BigDecimal.valueOf(difftmp);
	}
}