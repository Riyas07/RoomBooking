/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.UserDetailss.UserDetailss;
import com.ecommerce.ecommerce.model.LoginUser;
import com.ecommerce.ecommerce.repository.repositiryLogin;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
//import com.ecommerce.ecommerce.repository.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author ELCOT
 */
@Service
public class serv implements UserDetailsService{
@Autowired 
repositiryLogin repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username+" ................. ");
        LoginUser lu= repo.findByUsername(username);
        System.out.println(lu+"");
       if(lu==null)
       {
           System.out.println("LoginUser =="+lu);
           throw new UsernameNotFoundException(username);
       }
       return new UserDetailss(lu);
    }
    @Autowired
	private APIContext apiContext;
	public Payment createPayment(
			Double total, 
			String currency, 
			String method,
			String intent,
			String description, 
			String cancelUrl, 
			String successUrl) throws PayPalRESTException{
		Amount amount = new Amount();
                System.out.println("aaaaaaaaaaaaaaaaaaaa    "+amount.getTotal());
		amount.setCurrency(currency);
		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
                System.out.println("------------  "+total);
		amount.setTotal(String.format("%.2f", total));
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);
             System.out.println("transaction ---------------   "+transaction);
		Payer payer = new Payer();
		payer.setPaymentMethod(method.toString());
                 System.out.println("payer  payment method ,,,,,,,,,  "+payer.getPaymentMethod());
		Payment payment = new Payment();
		payment.setIntent(intent.toString());
                System.out.println("payment     ............   "+payment.getIntent());
		payment.setPayer(payer);  
                                System.out.println("payer   ................. "+payment.getPayer());
		payment.setTransactions(transactions);
                System.out.println("transaction  2222222222  ................. "+payment.getTransactions());
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);

		return payment.create(apiContext);
	}
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
                System.out.println("iddddddddddddddd  "+payment.getId());
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
                System.out.println("payer iid  ------- "+paymentExecute.getPayerId());
		return payment.execute(apiContext, paymentExecute);
	}

}
