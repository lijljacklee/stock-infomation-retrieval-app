import java.io.*;
import java.net.*;
import java.util.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.json.JSONObject;
import org.json.JSONArray;  
import org.json.JSONException; 


//import org.omg.CORBA.portable.InputStream;
//import javax.servlet.RequestDispatcher;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.net.URL;

public class GetAndPostRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String symbol = "";
		String urlString = "";
		// String type = (String) request.getParameter("type");

		symbol = request.getParameter("symbol");
		urlString = "http://default-environment-wxprjqfghv.elasticbeanstalk.com/index.php/?symbol="
				+ symbol;
		// address =
		// "http://default-environment-wxprjqfghv.elasticbeanstalk.com/?symbol="+symbol;
//		out.println("url:" + urlString + "\n");
		URL url = new URL(urlString);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setAllowUserInteraction(false);
		InputStream urlStream = url.openStream();
//		out.println(urlStream + "\n");
//		out.println(urlStream.toString());

//		out.println("Hello! This is Servlet!~\n");

		SAXBuilder builder = new SAXBuilder(false);
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonResult = new JSONObject();
		JSONObject jsonQuote = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonItem = new JSONObject();
		
		try {
//			out.println("I am here!");
			Document xml = builder.build(urlStream);
			Element root = xml.getRootElement();
			
//			List overList = root.getChildren();
//			out.println("~~~~~~~~~~~~~~~~~~"+overList.size());
//			for (int i=0;i<overList.size();i++){
//				out.println("++++++++++++++"+((Element)overList.get(i)).getText());
//			}
			
			Element quote = root.getChild("Quote");
//			out.println(quote.getText().toString());
			if (quote.getText().toString().equals("Information is not available.")){
				jsonResult.put("Quote",quote.getText());
//				out.println("YES!");
			}
			else{		
//				out.println("NO!");
//				out.println("----------------personal.xml----------------");
//				out.println("\nROOT:"+root.getText());
				List loverList = root.getChildren();
//				out.println("~~~~~~~~~~~~~~~~~~"+loverList.size());
//				for (int i=0;i<loverList.size();i++){
//					out.println("++++++++++++++"+((Element)loverList.get(i)).getText());
//				}
				//out.println(((Element)loverList.get(0)).getText());
				
//				for (int i=0;i<quote.size();i++){
//					out.println((Element)items.get(i)).getTags()+"......."+((Element)items.get(i)).getText());
//					jsonQuote.put((Element)items.get(i)).getTags(),(Element)items.get(i)).getText());
//				}
				jsonResult.put("Name",((Element)loverList.get(0)).getText());
				jsonResult.put("Symbol",((Element)loverList.get(1)).getText());
				
				jsonQuote.put("ChangeType",quote.getChild("ChangeType").getText());
				jsonQuote.put("Change",    quote.getChild("Change").getText());
				jsonQuote.put("ChangeInPercent",quote.getChild("ChangeInPercent").getText());
				jsonQuote.put("LastTradePriceOnly",quote.getChild("LastTradePriceOnly").getText());
				jsonQuote.put("PreviousClose",quote.getChild("PreviousClose").getText());
				jsonQuote.put("DaysLow",quote.getChild("DaysLow").getText());
				jsonQuote.put("DaysHigh",quote.getChild("DaysHigh").getText());
				jsonQuote.put("Open",quote.getChild("Open").getText());
				jsonQuote.put("YearLow",quote.getChild("YearLow").getText());
				jsonQuote.put("YearHigh",quote.getChild("YearHigh").getText());
				jsonQuote.put("Bid",quote.getChild("Bid").getText());
				jsonQuote.put("Volume",quote.getChild("Volume").getText());
				jsonQuote.put("Ask",quote.getChild("Ask").getText());
				jsonQuote.put("AverageDailyVolume",quote.getChild("AverageDailyVolume").getText());
				jsonQuote.put("OneYearTargetPrice",quote.getChild("OneYearTargetPrice").getText());
				jsonQuote.put("MarketCapitalization",quote.getChild("MarketCapitalization").getText());
				jsonResult.put("Quote",jsonQuote);
				
				Element news = root.getChild("News");
				if (news.getText().toString().equals("Rss feed not found")){
					jsonResult.put("News",news.getText());
				}
				else{
					List items = news.getChildren("Item");
//					Element result  = root.getChild("pre");
//					Element nameList = result.getChild("Name"); 
//					Element symbolList = result.getChild("Symbol");
					// List loverList = root.getChildren("lover");
					
					for (int i=0;i<items.size();i++){
//						out.println("---------------"+((Element)items.get(i)).getChild("Title").getText());
//						out.println("***************"+((Element)items.get(i)).getChild("Link").getText());
						JSONObject jsonObj2 = new JSONObject();
						jsonObj2.put("Title",((Element)items.get(i)).getChild("Title").getText());
						jsonObj2.put("Link",((Element)items.get(i)).getChild("Link").getText());
						jsonArr.put(jsonObj2);
					}
					jsonItem.put("Item",jsonArr);
					jsonResult.put("News",jsonItem);
				}
//				out.println("  name:  " + result.getChildText("Name") + "\n");
//				out.println(" Symbol: " + result.getChildText("Symbol"));
//				 for(int i =0;i<nameList.size();i++){
//					 out.println("Name:  "+((Element)nameList.get(i)).getText());
//					 out.println("Symbol: "+((Element)symbolList.get(i)).getText());
//					 out.println("  "+((Element)loverList.get(i)).getText());
//					 out.println();
//				 }
				jsonResult.put("StockChartImageURL",((Element)loverList.get(4)).getText());
				
			}
			jsonObj.put("result",jsonResult);
			out.println(jsonObj.toString());
			
		} catch (JSONException e) {    
	         // TODO Auto-generated catch block
	         e.printStackTrace();  
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
