package com.example.servlet;

import com.example.util.FileHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("loggedUser") != null;
    }

    // GET – show professional payment page
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) { res.sendRedirect("login.html?error=notloggedin"); return; }

        String orderId = req.getParameter("orderId");

        // Find order total from file
        double total = 0;
        String itemName = "", restaurantName = "";
        for (String line : FileHandler.readFromFile("orders.txt")) {
            String[] p = line.split("\\|");
            if (p.length >= 8 && p[0].equals(orderId)) {
                try { total = Double.parseDouble(p[5]); } catch (Exception e) {}
                restaurantName = p.length > 2 ? p[2] : "";
                itemName       = p.length > 3 ? p[3] : "";
                break;
            }
        }

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width,initial-scale=1'>");
        out.println("<title>Payment – Zeatly</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("<style>");
        out.println(".input-icon-wrap{position:relative}.input-icon{position:absolute;left:13px;top:50%;transform:translateY(-50%);font-size:16px;pointer-events:none}");
        out.println(".input-icon-wrap input{padding-left:38px}");
        out.println(".card-chips{display:flex;gap:8px;margin-bottom:18px;flex-wrap:wrap}");
        out.println(".chip{padding:6px 14px;border-radius:999px;border:1.5px solid var(--border);font-size:13px;font-weight:500;background:var(--surface2);color:var(--text3)}");
        out.println("</style></head><body>");
        out.println("<nav class='customer-nav'>");
        out.println("<a href='dashboard.html' class='brand'>🔥 Zeatly</a>");
        out.println("<div class='nav-links'><a href='order'>📦 My Orders</a><a href='logout' class='nav-logout'>Sign Out</a></div>");
        out.println("</nav>");
        out.println("<div class='customer-page'>");
        out.println("<div class='payment-container'>");
        out.println("<div class='payment-card animate-up'>");

        // Header
        out.println("<div class='payment-header'>");
        out.println("<div style='font-size:48px;margin-bottom:12px'>💳</div>");
        out.println("<h2>Secure Payment</h2>");
        out.println("<p>Order from <strong>" + restaurantName + "</strong></p>");
        out.println("</div>");

        // Amount block
        out.println("<div class='payment-amount'>");
        out.println("<div class='payment-amount-label'>Order Total</div>");
        out.println("<div class='payment-amount-value'>LKR " + String.format("%.2f", total) + "</div>");
        out.println("<div style='font-size:12px;opacity:0.7;margin-top:6px'>Order #" + orderId + " · " + itemName + "</div>");
        out.println("</div>");

        // Location field
        out.println("<div class='form-group'>");
        out.println("<label>📍 Delivery Address</label>");
        out.println("<div class='input-icon-wrap'><span class='input-icon'>📍</span>");
        out.println("<input type='text' id='deliveryAddr' placeholder='Enter your delivery address' style='padding-left:38px'></div>");
        out.println("</div>");

        // Payment method selector
        out.println("<label style='display:block;margin-bottom:12px;font-weight:600;font-size:14px;color:var(--text2)'>Choose Payment Method</label>");
        out.println("<div class='payment-methods'>");
        out.println("<div class='payment-method' id='pm-credit' onclick='selectMethod(\"credit\")'><div class='payment-method-icon'>💳</div><div class='payment-method-name'>Credit Card</div></div>");
        out.println("<div class='payment-method' id='pm-debit'  onclick='selectMethod(\"debit\")' ><div class='payment-method-icon'>🏧</div><div class='payment-method-name'>Debit Card</div></div>");
        out.println("<div class='payment-method' id='pm-cash'   onclick='selectMethod(\"cash\")'  ><div class='payment-method-icon'>💵</div><div class='payment-method-name'>Cash on Delivery</div></div>");
        out.println("</div>");

        // Card form
        out.println("<div class='card-form' id='cardForm'>");
        out.println("<div class='card-chips'><span class='chip'>Visa</span><span class='chip'>Mastercard</span><span class='chip'>Amex</span></div>");
        out.println("<div class='form-group'><label>Cardholder Name</label><div class='input-icon-wrap'><span class='input-icon'>👤</span><input type='text' id='cardName' placeholder='Name on card'></div></div>");
        out.println("<div class='form-group'><label>Card Number</label><div class='input-icon-wrap card-number-wrap'><span class='input-icon'>💳</span><input type='text' id='cardNum' placeholder='1234 5678 9012 3456' maxlength='19' oninput='fmtCard(this)'><span class='card-brand-icon' id='cardIcon'>💳</span></div></div>");
        out.println("<div class='form-row'>");
        out.println("<div class='form-group'><label>Expiry Date</label><div class='input-icon-wrap'><span class='input-icon'>📅</span><input type='text' id='cardExp' placeholder='MM / YY' maxlength='7' oninput='fmtExp(this)'></div></div>");
        out.println("<div class='form-group'><label>CVV</label><div class='input-icon-wrap'><span class='input-icon'>🔒</span><input type='password' id='cardCvv' placeholder='•••' maxlength='4'></div></div>");
        out.println("</div>");
        out.println("<form method='post' action='payment' id='cardPayForm'>");
        out.println("<input type='hidden' name='orderId' value='" + orderId + "'>");
        out.println("<input type='hidden' name='result' value='Success'>");
        out.println("<input type='hidden' name='method' value='card'>");
        out.println("<button type='button' class='btn btn-primary' style='width:100%;padding:16px;font-size:16px' onclick='submitCard()'>🔒 Pay LKR " + String.format("%.2f", total) + "</button>");
        out.println("</form>");
        out.println("</div>");

        // Cash form
        out.println("<div class='cash-confirm' id='cashForm'>");
        out.println("<div style='background:var(--surface3);border-radius:var(--radius-lg);padding:22px;text-align:center;margin-bottom:18px'>");
        out.println("<div style='font-size:48px;margin-bottom:10px'>🏍️</div>");
        out.println("<div style='font-family:Syne,sans-serif;font-size:18px;font-weight:700;margin-bottom:6px'>Cash on Delivery</div>");
        out.println("<div style='color:var(--text3);font-size:14px'>Our rider will collect <strong>LKR " + String.format("%.2f", total) + "</strong> when your order arrives. Please have the exact amount ready.</div>");
        out.println("</div>");
        out.println("<form method='post' action='payment'>");
        out.println("<input type='hidden' name='orderId' value='" + orderId + "'>");
        out.println("<input type='hidden' name='result' value='Success'>");
        out.println("<input type='hidden' name='method' value='cash'>");
        out.println("<button type='submit' class='btn btn-primary' style='width:100%;padding:16px;font-size:16px'>✅ Confirm Order</button>");
        out.println("</form>");
        out.println("</div>");

        out.println("<div style='text-align:center;margin-top:18px'>");
        out.println("<a href='order' style='color:var(--text3);font-size:13px'>← Back to Orders</a>");
        out.println("</div>");

        out.println("</div></div></div>");

        // Script
        out.println("<script>");
        out.println("function selectMethod(m){");
        out.println("  document.querySelectorAll('.payment-method').forEach(el=>el.classList.remove('selected'));");
        out.println("  document.getElementById('pm-'+m).classList.add('selected');");
        out.println("  document.getElementById('cardForm').classList.toggle('show', m==='credit'||m==='debit');");
        out.println("  document.getElementById('cashForm').classList.toggle('show', m==='cash');");
        out.println("}");
        out.println("function fmtCard(el){");
        out.println("  let v=el.value.replace(/\\D/g,'').substring(0,16);");
        out.println("  el.value=v.replace(/(\\d{4})(?=\\d)/g,'$1 ');");
        out.println("  const icon=document.getElementById('cardIcon');");
        out.println("  if(v.startsWith('4'))icon.textContent='💳';");
        out.println("  else if(v.startsWith('5')||v.startsWith('2'))icon.textContent='💳';");
        out.println("  else if(v.startsWith('3'))icon.textContent='🪙';");
        out.println("  else icon.textContent='💳';");
        out.println("}");
        out.println("function fmtExp(el){");
        out.println("  let v=el.value.replace(/\\D/g,'');");
        out.println("  if(v.length>=2)v=v.substring(0,2)+' / '+v.substring(2,4);");
        out.println("  el.value=v;");
        out.println("}");
        out.println("function submitCard(){");
        out.println("  const name=document.getElementById('cardName').value.trim();");
        out.println("  const num=document.getElementById('cardNum').value.replace(/\\s/g,'');");
        out.println("  const exp=document.getElementById('cardExp').value;");
        out.println("  const cvv=document.getElementById('cardCvv').value;");
        out.println("  const addr=document.getElementById('deliveryAddr').value.trim();");
        out.println("  if(!name){alert('Please enter the cardholder name.');return;}");
        out.println("  if(num.length<16){alert('Please enter a valid 16-digit card number.');return;}");
        out.println("  if(!exp.includes('/')){alert('Please enter a valid expiry date.');return;}");
        out.println("  if(cvv.length<3){alert('Please enter a valid CVV.');return;}");
        out.println("  if(!addr){alert('Please enter your delivery address.');return;}");
        out.println("  document.getElementById('cardPayForm').submit();");
        out.println("}");
        out.println("</script>");
        out.println("</body></html>");
    }

    // POST – update payment status
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) { res.sendRedirect("login.html?error=notloggedin"); return; }

        String orderId = req.getParameter("orderId").trim();
        String result  = req.getParameter("result").trim();
        String method  = req.getParameter("method") != null ? req.getParameter("method").trim() : "card";

       
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length == 8 && parts[0].equals(orderId)) {
                parts[7] = result;
                updated.add(String.join("|", parts));
            } else {
                updated.add(line);
            }
        }

        String filePath = FileHandler.getBaseDir() + "orders.txt";
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : updated) { w.write(line); w.newLine(); }
        }

        res.sendRedirect("order?payment=" + result + "&method=" + method);
    }
}
