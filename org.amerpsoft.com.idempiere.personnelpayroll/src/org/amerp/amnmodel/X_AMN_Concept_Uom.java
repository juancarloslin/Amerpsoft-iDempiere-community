/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.amerp.amnmodel;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Model for AMN_Concept_Uom
 *  @author iDempiere (generated) 
 *  @version Release 2.1 - $Id$ */
public class X_AMN_Concept_Uom extends PO implements I_AMN_Concept_Uom, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20151113L;

    /** Standard Constructor */
    public X_AMN_Concept_Uom (Properties ctx, int AMN_Concept_Uom_ID, String trxName)
    {
      super (ctx, AMN_Concept_Uom_ID, trxName);
      /** if (AMN_Concept_Uom_ID == 0)
        {
			setAMN_Concept_Uom_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_AMN_Concept_Uom (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 6 - System - Client 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_AMN_Concept_Uom[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Payroll Concepts UOM.
		@param AMN_Concept_Uom_ID Payroll Concepts UOM	  */
	public void setAMN_Concept_Uom_ID (int AMN_Concept_Uom_ID)
	{
		if (AMN_Concept_Uom_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_AMN_Concept_Uom_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AMN_Concept_Uom_ID, Integer.valueOf(AMN_Concept_Uom_ID));
	}

	/** Get Payroll Concepts UOM.
		@return Payroll Concepts UOM	  */
	public int getAMN_Concept_Uom_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AMN_Concept_Uom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set AMN_Concept_Uom_UU.
		@param AMN_Concept_Uom_UU AMN_Concept_Uom_UU	  */
	public void setAMN_Concept_Uom_UU (String AMN_Concept_Uom_UU)
	{
		set_ValueNoCheck (COLUMNNAME_AMN_Concept_Uom_UU, AMN_Concept_Uom_UU);
	}

	/** Get AMN_Concept_Uom_UU.
		@return AMN_Concept_Uom_UU	  */
	public String getAMN_Concept_Uom_UU () 
	{
		return (String)get_Value(COLUMNNAME_AMN_Concept_Uom_UU);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}