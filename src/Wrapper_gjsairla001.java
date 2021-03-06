import com.qunar.qfwrapper.bean.booking.BookingResult;
import com.qunar.qfwrapper.bean.booking.BookingInfo;
import com.qunar.qfwrapper.bean.search.FlightSearchParam;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.bean.search.OneWayFlightInfo;
import com.qunar.qfwrapper.bean.search.FlightDetail;
import com.qunar.qfwrapper.bean.search.FlightSegement;
import com.qunar.qfwrapper.bean.search.RoundTripFlightInfo;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;
import com.qunar.qfwrapper.util.QFPostMethod;
import com.qunar.qfwrapper.constants.Constants;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wrapper_gjsairla001 implements QunarCrawler{
	private static String cookie = "";
		
	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		searchParam.setDep("CRD");
		searchParam.setArr("CPO");
		searchParam.setDepDate("2014-09-03");
		searchParam.setRetDate("2014-09-18");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		
		String html = new  Wrapper_gjsairla001().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new  Wrapper_gjsairla001().process(html,searchParam);
		if(result.isRet() && result.getStatus().equals(Constants.SUCCESS))
		{
			List<RoundTripFlightInfo> flightList = (List<RoundTripFlightInfo>) result.getData();
			for (RoundTripFlightInfo in : flightList){
				System.out.println(in.getInfo().toString());
				System.out.println(in.getDetail().toString());
				System.out.println(in.toString());
			}
		}
		else
		{
			System.out.println(result.getStatus());
		}
	}
	
	public BookingResult getBookingInfo(FlightSearchParam arg0) {

		String bookingUrlPre = "http://booking.lan.com/cgi-bin/compra/paso2.cgi?url_promo=&reserva=&otras_ciudades=&num_segmentos_interfaz=2&from_city1=%s&to_city1=%s&from_city2=%s&to_city2=%s&nadults=1&nchildren=0&ninfants=0&tipo_paso2=flex&flex=0&no_tarifas_promocionales=1&fecha1_dia=%s&fecha1_anomes=%s&fecha2_dia=%s&fecha2_anomes=%s&mas_barato_owflex_ida=1&mas_barato_owflex_vuelta=2&mas_barato_owflex_ida_farebasis=NEESPD6K-LA&mas_barato_owflex_vuelta_farebasis=YEEFFZ0K-LA";
		BookingResult bookingResult = new BookingResult();
		
		String[] depStr = arg0.getDepDate().split("-");
		String[] arrStr = arg0.getRetDate().split("-");
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put("from_city1",arg0.getDep());
		map.put("to_city1",arg0.getArr());
		map.put("from_city2",arg0.getArr());
		map.put("to_city2",arg0.getDep());
		map.put("fecha1_dia",depStr[2]);
		map.put("fecha1_anomes",depStr[0] + "-" + depStr[1]);
		map.put("fecha2_dia",arrStr[2]);
		map.put("fecha2_anomes",arrStr[0] + "-" + arrStr[1]);
		map.put("url_promo","");
		map.put("reserva","");
		map.put("otras_ciudades","");
		map.put("num_segmentos_interfaz","1");
		map.put("nchildren","0");
		map.put("ninfants","0");
		map.put("tipo_paso2","flex");
		map.put("flex","0");
		map.put("no_tarifas_promocionales","1");
		map.put("mas_barato_owflex_ida","1");
		map.put("mas_barato_owflex_ida_farebasis","HEEFXZ0K-LA");
		map.put("mas_barato_owflex_vuelta","");
		map.put("mas_barato_owflex_vuelta_farebasis","YEEFFZ0K-LA");
		
		bookingInfo.setInputs(map);		
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;
	}

	public String getHtml(FlightSearchParam arg0) {
		QFGetMethod get = null;	
		try {
			
			String[] depStr = arg0.getDepDate().split("-");
			String[] arrStr = arg0.getRetDate().split("-");
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			httpClient.getHostConfiguration().setHost("booking.lan.com");
			
			String baseUrl = String.format("http://booking.lan.com/cgi-bin/compra/paso2.cgi?fecha1_dia=%s&fecha1_anomes=%s&fecha2_dia=%s&fecha2_anomes=%s&otras_ciudades=&num_segmentos_interfaz=2&tipo_paso1=caja&from_city2=%s&to_city2=%s&from_city1=%s&to_city1=%s&auAvailability=1&ida_vuelta=ida_vuelta&flex=1&cabina=Y&nadults=1&nchildren=0&ninfants=0",depStr[2],depStr[0] + "-" + depStr[1],arrStr[2],arrStr[0] + "-" + arrStr[1],arg0.getArr(),arg0.getDep(),arg0.getDep(),arg0.getArr());
			String getUrl = String.format("http://booking.lan.com/cgi-bin/compra/paso2.cgi?url_promo=&reserva=&otras_ciudades=&num_segmentos_interfaz=2&from_city1=%s&to_city1=%s&from_city2=%s&to_city2=%s&nadults=1&nchildren=0&ninfants=0&tipo_paso2=flex&flex=0&no_tarifas_promocionales=1&fecha1_dia=%s&fecha1_anomes=%s&fecha2_dia=%s&fecha2_anomes=%s&mas_barato_owflex_ida=1&mas_barato_owflex_vuelta=2&mas_barato_owflex_ida_farebasis=NEESPD6K-LA&mas_barato_owflex_vuelta_farebasis=YEEFFZ0K-LA",arg0.getDep(),arg0.getArr(),arg0.getArr(),arg0.getDep(),depStr[2],depStr[0] + "-" + depStr[1],arrStr[2],arrStr[0] + "-" + arrStr[1]);
			
			get = new QFGetMethod(baseUrl);
			get.setRequestHeader("Referer", "http://www.lan.com/en_us/sitio_personas/index.html");
			get.getParams().setContentCharset("UTF-8");
			httpClient.executeMethod(get);
			get.releaseConnection();
			
			cookie = StringUtils.join(httpClient.getState().getCookies(),"; ");
			get = new QFGetMethod(getUrl);
			httpClient.getState().clearCookies();
			get.addRequestHeader("Cookie",cookie);
			httpClient.executeMethod(get);
		    
		    
		    return get.getResponseBodyAsString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (null != get){
				get.releaseConnection();
			}
		}
		return "Exception";
	}
	
	
	public String getPriceHtml(FlightSearchParam arg0,String getUrl,String outid,String retid,String vs1,String vs2) {
		QFPostMethod post = null;	
		try {
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			httpClient.getHostConfiguration().setHost("booking.lan.com");	
			
			post = new QFPostMethod(getUrl);
			
			String[] sid = getUrl.split("=");
			NameValuePair[] names = {
					new NameValuePair("session_id",sid[2]),
					new NameValuePair("tipo_paso2","normal"),
					new NameValuePair("HORARIO_SEG1_VUE0",outid),
					new NameValuePair("HORARIO_SEG2_VUE0",retid),
					new NameValuePair("CPLP_VALID_SEG1_VUE1",""),
					new NameValuePair("vuelo_segmento1",vs1),
					new NameValuePair("vuelo_segmento2",vs2),
					new NameValuePair("tipo_paso2","normal"),
			};
			
			post.setRequestBody(names);
			post.setRequestHeader("Host", "booking.lan.com");
			post.getParams().setContentCharset("UTF-8");
			post.addRequestHeader("Cookie",cookie);
		    httpClient.executeMethod(post);
		    return post.getResponseBodyAsString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (null != post){
				post.releaseConnection();
			}
		}
		return "Exception";
	}


	public ProcessResultInfo process(String html, FlightSearchParam param) {
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;			
		}		
		//需要有明显的提示语句，才能判断是否INVALID_DATE|INVALID_AIRLINE|NO_RESULT
		if (html.contains("Today Flight is full, select an other day or check later for any seat released. ")) {
			result.setRet(false);
			result.setStatus(Constants.INVALID_DATE);
			return result;			
		}
		
		try {
			return parser(html, param);
		} catch(Exception e){
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
			return result;
		}
	}
	
	
	public ProcessResultInfo parser(String html, FlightSearchParam param) {
		ProcessResultInfo result = new ProcessResultInfo();
		String[] jsonStr = StringUtils.substringsBetween(html, "<table class=\"table\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">", "</table>");
		
		String Icon = "USD";
		String tmpPrice = "";
		String taxStr = "";
		String leavTime = "";
		String landTime = "";
		
		Hashtable dict = new Hashtable();
		dict.put("january", "01");
		dict.put("february", "02");
		dict.put("march1", "03");
		dict.put("april", "04");
		dict.put("may", "05");
		dict.put("june", "06");
		dict.put("july", "07");
		dict.put("august", "08");
		dict.put("september", "09");
		dict.put("october", "10");
		dict.put("november1", "11");
		dict.put("december", "12");
		
		String[] outboundFlights = null;
		String[] returnFlights = null;
		
		try{
			outboundFlights = StringUtils.substringsBetween(jsonStr[0], "HORARIO_SEG", "</tr>");
			returnFlights = StringUtils.substringsBetween(jsonStr[2], "HORARIO_SEG", "</tr>");
		}catch (Exception e){
			outboundFlights = null;
			returnFlights = null;
		}
		
		
		DecimalFormat df = new DecimalFormat("#.00");
		
		List<RoundTripFlightInfo> flightList = new ArrayList<RoundTripFlightInfo>();
		//去程信息
		if (outboundFlights==null){
			String getUrl = StringUtils.substringBetween(html,"f.action='","';");
			String hid = StringUtils.substringBetween(html,"name='HORARIO_SEG1_VUE0' value='","'>");
//			String priceHtml = getPriceHtml(param, getUrl,hid);
//			
//			System.err.println(priceHtml);
//			
//			String tmpStr = StringUtils.substringBetween(priceHtml,"Total</td>","US$");
//			String[] td = StringUtils.substringsBetween(tmpStr,"<td style=\"padding: 0 8px 0 8px;\">","</td>");
			
			System.err.println(html);
			String[] outRet = StringUtils.substringsBetween(html, "<table class=table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">", "</table>");
			
			outboundFlights = StringUtils.substringsBetween(outRet[0], "tr onMouseDown=", "</tr>");
			returnFlights = StringUtils.substringsBetween(outRet[1], "tr onMouseDown=", "</tr>");
			
			
			for(String outF : outboundFlights){
					
					List<FlightSegement> segs = new ArrayList<FlightSegement>();
					List<String> flightNoList = new ArrayList<String>();
					String oid = StringUtils.substringBetween(outF,"name='HORARIO_SEG1_VUE",">");
					String outid = StringUtils.substringBetween(oid,"value='","'");
					String vseg1 = StringUtils.substringBetween(outF,"vuelo_segmento",">");
					String vs1 = StringUtils.substringBetween(vseg1,"value='","'");
					
					String arrDate = "";
					String[] flightOut = StringUtils.substringsBetween(outF, "style=\"margin-right:5px;\">", "</span>");
					String[] strTime = StringUtils.substringsBetween(outF, "<td>", "</td>");
					leavTime = StringUtils.substringBetween(strTime[2], "", "<br");
					String tmpLandTime = StringUtils.substringBetween(strTime[3], "(", ")");
					String lt = StringUtils.substringBetween(strTime[3], "", "<br");
					landTime = lt.trim();
					if (tmpLandTime==null || tmpLandTime.startsWith("typeof" )){
						arrDate = param.getDepDate();
					}else{
						String[] tt = tmpLandTime.split(" ");
						String day = tt[0];
						String mon = tt[1];
						String[] dateStr = param.getDepDate().split("-");
						arrDate = dateStr[0] + "-" + dict.get(mon) + "-" + day;
					}
					
					for (String fn:flightOut){
						String tmp_fn = StringUtils.substringBetween(fn, "", "(");
						String flightNo = tmp_fn.replaceAll("[^a-zA-Z\\d]", "");
						if (!flightNoList.contains(flightNo)){
							FlightSegement seg = new FlightSegement(flightNo);
							seg.setDeptime(leavTime);
							seg.setArrtime(landTime);
							
							seg.setDepDate(param.getDepDate());
							seg.setArrDate(arrDate);
							seg.setDepairport(param.getDep());
							seg.setArrairport(param.getArr());
							
							segs.add(seg);
							flightNoList.add(flightNo);
						}
					}
					
					
					// 返程信息
					for (String retF : returnFlights) {
						
							RoundTripFlightInfo flight = new RoundTripFlightInfo();
							// 返程航段
							List<FlightSegement> retSegs = new ArrayList<FlightSegement>();
							// 返程航班号
							List<String> flightNoRetList = new ArrayList<String>();
							FlightDetail flightDetail = new FlightDetail();
							
							String rid = StringUtils.substringBetween(retF,"name='HORARIO_SEG2_VUE",">");
							String retid = StringUtils.substringBetween(rid,"value='","'");
							String vseg2 = StringUtils.substringBetween(retF,"vuelo_segmento",">");
							String vs2 = StringUtils.substringBetween(vseg2,"value='","'");
							
							String retDate = "";
							String[] flightRet = StringUtils.substringsBetween(retF, "style=\"margin-right:5px;\">", "</span>");
							String[] strRetTime = StringUtils.substringsBetween(retF, "<td>", "</td>");
							leavTime = StringUtils.substringBetween(strRetTime[2], "", "<br");
							String tmpRetTime = StringUtils.substringBetween(strRetTime[3], "(", ")");
							String retStr = StringUtils.substringBetween(strRetTime[3], "", "<br");
							landTime = retStr.trim();
							if (tmpRetTime==null || tmpRetTime.startsWith("typeof" )){
								retDate = param.getDepDate();
							}else{
								String[] tt = tmpRetTime.split(" ");
								String day = tt[0];
								String mon = tt[1];
								String[] dateStr = param.getDepDate().split("-");
								retDate = dateStr[0] + "-" + dict.get(mon) + "-" + day;
							}
							
							for (String fn:flightRet){
								String tmp_fn = StringUtils.substringBetween(fn, "", "(");
								String flightNo = tmp_fn.replaceAll("[^a-zA-Z\\d]", "");
								if (!flightNoList.contains(flightNo)){
									FlightSegement reseg = new FlightSegement(flightNo);
									reseg.setDeptime(leavTime);
									reseg.setArrtime(landTime);
									
									reseg.setDepDate(param.getDepDate());
									reseg.setArrDate(arrDate);
									reseg.setDepairport(param.getDep());
									reseg.setArrairport(param.getArr());
									
									retSegs.add(reseg);
									flightNoRetList.add(flightNo);
								}
							}
							
							
							String priceHtml = getPriceHtml(param, getUrl,outid,retid,vs1,vs2);
							System.err.println(priceHtml);
							String tmpStr = StringUtils.substringBetween(priceHtml,"Total</td>","US$");
							String[] td = StringUtils.substringsBetween(tmpStr,"<td style=\"padding: 0 8px 0 8px;\">","</td>");
							double pri = Double.parseDouble(td[1].replace(",", ""));
							
							// 航班详细信息
							flightDetail.setDepdate(Date.valueOf(param.getDepDate()));
							flightDetail.setFlightno(flightNoList);
							flightDetail.setMonetaryunit(Icon);
							// 总价
							flightDetail.setPrice(Double.parseDouble(df.format(pri)));
							flightDetail.setTax(Double.parseDouble(df.format(Double.parseDouble(td[2]))));
							flightDetail.setDepcity(param.getDep());
							flightDetail.setArrcity(param.getArr());
							flightDetail.setWrapperid(param.getWrapperid());
							
							//去程价格
//							flight.setOutboundPrice(outPrice);
							//返程价格
//							flight.setReturnedPrice(returnPrice);
							flight.setDetail(flightDetail);
							flight.setRetdepdate(Date.valueOf(param.getRetDate()));
							flight.setInfo(segs);
							flight.setRetinfo(retSegs);
							flight.setRetflightno(flightNoRetList);
							
							flightList.add(flight);
					}
			}
			
			
			
		}else{
			for(String outF : outboundFlights){
				// 去程航段
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				// 去程航班号
				List<String> flightNoList = new ArrayList<String>();
					
					String[] flightStrings = StringUtils.substringsBetween(outF, "<strong>", "</strong>");
					String[] strTime = StringUtils.substringsBetween(outF, "<span style=\"vertical-align:top\">", "</span>");
					leavTime = strTime[0];
					landTime = StringUtils.substringBetween(strTime[1], "", "<br");
					
					String go_price = getPrice(outF);
					taxStr = StringUtils.substringBetween(outF, "Array($H({CO:$H({", "aplicacion:'FARE'})})))");
					String[] notAvailable = StringUtils.substringsBetween(outF, "Not available", "</p>");
					if (taxStr==null){
						taxStr = StringUtils.substringBetween(outF, "Array($H({DG:$H({", "aplicacion:'FARE'})})))");
					}
					if (notAvailable!=null && notAvailable.length==5){
						continue;
					}
					
					String[] taxmono = StringUtils.substringsBetween(taxStr, "monto:", " ,");
					String[] taxporcentaje = StringUtils.substringsBetween(taxStr, "porcentaje:", ",");
					
					double mono = 0.00;
					for (String ta:taxmono){
						mono += Double.parseDouble(ta.replace(",", ""));
					}
					
					double porcentaje = 0.00;
					for (String ta:taxporcentaje){
						porcentaje += Double.parseDouble(ta.replace(",", ""));
					}
					
					double outtaxPrice = mono + Double.parseDouble(go_price.replace(",", "")) * porcentaje / 100;
					
					String day = StringUtils.substringBetween(strTime[1], ">(", " ");
					String mon = StringUtils.substringBetween(strTime[1], " ", ")");
					String[] dateStr = param.getDepDate().split("-");
					String arrDate = dateStr[0] + "-" + dict.get(mon) + "-" + day;
					
					for (String fn:flightStrings){
						String flightNo = fn.replaceAll("[^a-zA-Z\\d]", "");
						if (!flightNoList.contains(flightNo)){
							FlightSegement seg = new FlightSegement(flightNo);
							seg.setDeptime(leavTime);
							seg.setArrtime(landTime);
							
							seg.setDepDate(param.getDepDate());
							seg.setArrDate(arrDate);
							seg.setDepairport(param.getDep());
							seg.setArrairport(param.getArr());
							
							segs.add(seg);
							flightNoList.add(flightNo);
						}
					}
					
					// 返程信息
					for (String retF : returnFlights) {
						
							RoundTripFlightInfo flight = new RoundTripFlightInfo();
							// 返程航段
							List<FlightSegement> retSegs = new ArrayList<FlightSegement>();
							// 返程航班号
							List<String> flightNoRetList = new ArrayList<String>();
							FlightDetail flightDetail = new FlightDetail();
							
							String[] reflightStrings = StringUtils.substringsBetween(retF, "<strong>", "</strong>");
							String[] strretTime = StringUtils.substringsBetween(retF, "<span style=\"vertical-align:top\">", "</span>");
							leavTime = strretTime[0];
							landTime = StringUtils.substringBetween(strretTime[1], "", "<br");
							
							String arr_day = StringUtils.substringBetween(strretTime[1], ">(", " ");
							String arr_mon = StringUtils.substringBetween(strretTime[1], " ", ")");
							String retDate = dateStr[0] + "-" + dict.get(arr_mon) + "-" + arr_day;
							
							String re_price = getPrice(retF);
							if (re_price == ""){
								continue;
							}
							taxStr = StringUtils.substringBetween(retF, "Array($H({QI:$H({", "aplicacion:'FARE'})}))");
							String[] rettaxmono = StringUtils.substringsBetween(taxStr, "monto:", " ,");
							String[] rettaxporcentaje = StringUtils.substringsBetween(taxStr, "porcentaje:", ",");
							
							
							double monoret = 0.00;
							for (String ta:rettaxmono){
								monoret += Double.parseDouble(ta.replace(",", ""));
							}
							
							double porcentajeret = 0.00;
							for (String ta:rettaxporcentaje){
								porcentajeret += Double.parseDouble(ta.replace(",", ""));
							}
							
							double returntaxPrice = monoret + Double.parseDouble(re_price.replace(",", "")) * porcentajeret / 100;
							
							for (String fn:reflightStrings){
								String flightNo = fn.replaceAll("[^a-zA-Z\\d]", "");
								if (!flightNoRetList.contains(flightNo)){
									FlightSegement reseg = new FlightSegement(flightNo);
									reseg.setDeptime(leavTime);
									reseg.setArrtime(landTime);
									
									reseg.setDepDate(param.getRetDate());
									reseg.setArrDate(retDate);
									reseg.setDepairport(param.getDep());
									reseg.setArrairport(param.getArr());
									
									retSegs.add(reseg);
									flightNoRetList.add(flightNo);
								}
							}
							
							
							
							double outPrice = Double.parseDouble(go_price.replace(",", ""));
							double returnPrice = Double.parseDouble(re_price.replace(",", ""));
							double total = Math.ceil(outPrice + returnPrice);
							
							double totalTax = outtaxPrice + returntaxPrice;
							
							// 航班详细信息
							flightDetail.setDepdate(Date.valueOf(param.getDepDate()));
							flightDetail.setFlightno(flightNoList);
							flightDetail.setMonetaryunit(Icon);
							// 总价
							flightDetail.setPrice(Double.parseDouble(df.format(total)));
							flightDetail.setTax(Double.parseDouble(df.format(totalTax)));
							flightDetail.setDepcity(param.getDep());
							flightDetail.setArrcity(param.getArr());
							flightDetail.setWrapperid(param.getWrapperid());
							
							//去程价格
							flight.setOutboundPrice(outPrice);
							//返程价格
							flight.setReturnedPrice(returnPrice);
							flight.setDetail(flightDetail);
							flight.setRetdepdate(Date.valueOf(param.getRetDate()));
							flight.setInfo(segs);
							flight.setRetinfo(retSegs);
							flight.setRetflightno(flightNoRetList);
							
							flightList.add(flight);
					}
			}
		}
		result.setRet(true);
		result.setStatus(Constants.SUCCESS);
		result.setData(flightList);
		return result;
	}
	
	public String getPrice(String html) {
		String price = "";
		String[] tmpPrice = StringUtils.substringsBetween(html, "tdfamilia", "</td>");
		for (String tp:tmpPrice){
			String strPrice = StringUtils.substringBetween(tp, "valor_tarifa_seg", "/>");
			if (strPrice != null){
				price = StringUtils.substringBetween(strPrice, "value=\"", "\"");
				break;
			}
		}
		return price;
	}
}
