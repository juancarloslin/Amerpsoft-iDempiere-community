/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2008 e-Evolution,SC. All Rights Reserved.               *
 * Contributor(s): Victor Perez www.e-evolution.com                           *
 *****************************************************************************/
package org.amerp.amndocument;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.compiere.model.MBankAccount;
import org.compiere.model.MPayment;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.Env;
 
/**
 *  Bank Transfer. Generate two Payments entry
 *  
 *  For Bank Transfer From Bank Account "A" 
 *                 
 *	@author victor.perez@e-evoltuion.com
 *	
 **/
public class BankTransfer extends SvrProcess
{
	private String 		p_DocumentNo= "";				// Document No
	private String 		p_Description= "";				// Description
	private int 		p_C_BPartner_ID = 0;   			// Business Partner to be used as bridge
	private int		p_C_Currency_ID = 0;			// Payment Currency
	private int 		p_C_ConversionType_ID = 0;		// Payment Conversion Type
	private int		p_C_Charge_ID = 0;				// Charge to be used as bridge

	private BigDecimal 	p_Amount = Env.ZERO;  			// Amount to be transfered between the accounts
	private int 		p_From_C_BankAccount_ID = 0;	// Bank Account From
	private int 		p_To_C_BankAccount_ID= 0;		// Bank Account To
	private Timestamp	p_StatementDate = null;  		// Date Statement
	private Timestamp	p_DateAcct = null;  			// Date Account
	private int         m_created = 0;
	private MPayment paymentBankFrom;
	private MPayment paymentBankTo;
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("From_C_BankAccount_ID"))
				p_From_C_BankAccount_ID = para[i].getParameterAsInt();
			else if (name.equals("To_C_BankAccount_ID"))
				p_To_C_BankAccount_ID = para[i].getParameterAsInt();
			else if (name.equals("C_BPartner_ID"))
				p_C_BPartner_ID = para[i].getParameterAsInt();
			else if (name.equals("C_Currency_ID"))
				p_C_Currency_ID = para[i].getParameterAsInt();
			else if (name.equals("C_ConversionType_ID"))
				p_C_ConversionType_ID = para[i].getParameterAsInt();
			else if (name.equals("C_Charge_ID"))
				p_C_Charge_ID = para[i].getParameterAsInt();
			else if (name.equals("DocumentNo"))
				p_DocumentNo = (String)para[i].getParameter();
			else if (name.equals("Amount"))
				p_Amount = ((BigDecimal)para[i].getParameter());	
			else if (name.equals("Description"))
				p_Description = (String)para[i].getParameter();
			else if (name.equals("StatementDate"))
				p_StatementDate = (Timestamp)para[i].getParameter();
			else if (name.equals("DateAcct"))
				p_DateAcct = (Timestamp)para[i].getParameter();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (translated text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		if (log.isLoggable(Level.INFO)) log.info("From Bank="+p_From_C_BankAccount_ID+" - To Bank="+p_To_C_BankAccount_ID
				+ " - C_BPartner_ID="+p_C_BPartner_ID+"- C_Charge_ID= "+p_C_Charge_ID+" - Amount="+p_Amount+" - DocumentNo="+p_DocumentNo
				+ " - Description="+p_Description+ " - Statement Date="+p_StatementDate+
				" - Date Account="+p_DateAcct);

		if (p_To_C_BankAccount_ID == 0 || p_From_C_BankAccount_ID == 0)
			throw new IllegalArgumentException("Banks required");

		if (p_DocumentNo == null || p_DocumentNo.length() == 0)
			throw new IllegalArgumentException("Document No required");

		if (p_To_C_BankAccount_ID == p_From_C_BankAccount_ID)
			throw new AdempiereUserError ("Banks From and To must be different");
		
		if (p_C_BPartner_ID == 0)
			throw new AdempiereUserError ("Business Partner required");
		
		if (p_C_Currency_ID == 0)
			throw new AdempiereUserError ("Currency required");
		
		if (p_C_Charge_ID == 0)
			throw new AdempiereUserError ("Business Partner required");
	
		if (p_Amount.signum() == 0)
			throw new AdempiereUserError ("Amount required");

		//	Login Date
		if (p_StatementDate == null)
			p_StatementDate = Env.getContextAsDate(getCtx(), "#Date");
		if (p_StatementDate == null)
			p_StatementDate = new Timestamp(System.currentTimeMillis());			

		if (p_DateAcct == null)
			p_DateAcct = p_StatementDate;

		generateBankTransfer();

		// example about how to use the new feature IDEMPIERE-1773 Asking for input from within a process
		final StringBuffer yesno = new StringBuffer();
		final StringBuffer string = new StringBuffer();
		final StringBuffer stringcaptured = new StringBuffer();
		processUI.ask("Process went fine, do you want to commit?", new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				addLog("You decided to " + (result ? "commit" : "rollback"));
				yesno.append(result);
				if (result) {
					processUI.askForInput("Please enter authorization code from bank to put in description:", new Callback<String>() {
						@Override
						public void onCallback(String result) {
							addLog("You entered authorization code: " + result);
							string.append(result);
							stringcaptured.append("true");
						}
					});
				}
			}
		});
		//wait for answer
		// please note at this moment iDempiere has locks on many tables, some with high-contention,
		// this is why a timeout is implemented
		waitFor(yesno, 5);
		if ("false".equals(yesno.toString())) {
			throw new AdempiereUserError("User decided to rollback");
		} else {
			waitFor(stringcaptured, 5);
			paymentBankFrom.setDescription(string.toString());
			paymentBankFrom.saveEx();
			paymentBankTo.setDescription(string.toString());
			paymentBankFrom.saveEx();
		}
		// end of example

		return "@Created@ = " + m_created;
	}	//	doIt

	private void waitFor(StringBuffer string, int timeoutInSeconds) {
		int sleepms = 200;
		int maxcycles = timeoutInSeconds * 1000 / sleepms;
		int cycles = 0;
		while (string.length() == 0) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
			cycles++;
			if (cycles > maxcycles)
				throw new AdempiereUserError("Timeout waiting for user answer");
		}
	}

	/**
	 * Generate BankTransfer()
	 *
	 */
	private void generateBankTransfer()
	{

		MBankAccount mBankFrom = new MBankAccount(getCtx(),p_From_C_BankAccount_ID, get_TrxName());
		MBankAccount mBankTo = new MBankAccount(getCtx(),p_To_C_BankAccount_ID, get_TrxName());
		
		paymentBankFrom = new MPayment(getCtx(), 0 ,  get_TrxName());
		paymentBankFrom.setC_BankAccount_ID(mBankFrom.getC_BankAccount_ID());
		paymentBankFrom.setDocumentNo(p_DocumentNo);
		paymentBankFrom.setDateAcct(p_DateAcct);
		paymentBankFrom.setDateTrx(p_StatementDate);
		paymentBankFrom.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
		paymentBankFrom.setDescription(p_Description);
		paymentBankFrom.setC_BPartner_ID (p_C_BPartner_ID);
		paymentBankFrom.setC_Currency_ID(p_C_Currency_ID);
		if (p_C_ConversionType_ID > 0)
			paymentBankFrom.setC_ConversionType_ID(p_C_ConversionType_ID);	
		paymentBankFrom.setPayAmt(p_Amount);
		paymentBankFrom.setOverUnderAmt(Env.ZERO);
		paymentBankFrom.setC_DocType_ID(false);
		paymentBankFrom.setC_Charge_ID(p_C_Charge_ID);
		paymentBankFrom.saveEx();
		if(!paymentBankFrom.processIt(MPayment.DOCACTION_Complete)) {
			log.warning("Payment Process Failed: " + paymentBankFrom + " - " + paymentBankFrom.getProcessMsg());
			throw new IllegalStateException("Payment Process Failed: " + paymentBankFrom + " - " + paymentBankFrom.getProcessMsg());
		}
		paymentBankFrom.saveEx();
		addBufferLog(paymentBankFrom.getC_Payment_ID(), paymentBankFrom.getDateTrx(),
				null, paymentBankFrom.getC_DocType().getName() + " " + paymentBankFrom.getDocumentNo(),
				MPayment.Table_ID, paymentBankFrom.getC_Payment_ID());
		m_created++;

		paymentBankTo = new MPayment(getCtx(), 0 ,  get_TrxName());
		paymentBankTo.setC_BankAccount_ID(mBankTo.getC_BankAccount_ID());
		paymentBankTo.setDocumentNo(p_DocumentNo);
		paymentBankTo.setDateAcct(p_DateAcct);
		paymentBankTo.setDateTrx(p_StatementDate);
		paymentBankTo.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
		paymentBankTo.setDescription(p_Description);
		paymentBankTo.setC_BPartner_ID (p_C_BPartner_ID);
		paymentBankTo.setC_Currency_ID(p_C_Currency_ID);
		if (p_C_ConversionType_ID > 0)
			paymentBankFrom.setC_ConversionType_ID(p_C_ConversionType_ID);	
		paymentBankTo.setPayAmt(p_Amount);
		paymentBankTo.setOverUnderAmt(Env.ZERO);
		paymentBankTo.setC_DocType_ID(true);
		paymentBankTo.setC_Charge_ID(p_C_Charge_ID);
		paymentBankTo.saveEx();
		if (!paymentBankTo.processIt(MPayment.DOCACTION_Complete)) {
			log.warning("Payment Process Failed: " + paymentBankTo + " - " + paymentBankTo.getProcessMsg());
			throw new IllegalStateException("Payment Process Failed: " + paymentBankTo + " - " + paymentBankTo.getProcessMsg());
		}
		paymentBankTo.saveEx();
		addBufferLog(paymentBankTo.getC_Payment_ID(), paymentBankTo.getDateTrx(),
				null, paymentBankTo.getC_DocType().getName() + " " + paymentBankTo.getDocumentNo(),
				MPayment.Table_ID, paymentBankTo.getC_Payment_ID());
		m_created++;
		return;

	}  //  createCashLines
	
}	//	ImmediateBankTransfer