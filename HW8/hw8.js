function Share(){
			var companyName = document.getElementById("companyName").value;
			var symbol = document.getElementById("symbol").innerHTML;
			var lastTrade = document.getElementById("LastTradePriceOnly").innerHTML;
			var change = document.getElementById("change").innerHTML;
			var changeType = document.getElementById("changeType").innerHTML == "<image src='http://www-scf.usc.edu/~csci571/2014Spring/hw6/down_r.gif' />"?"-":"+";
			var change = document.getElementById("change").innerHTML;
			var imageUrl = document.getElementById("imageUrl").src;
			
			window.fbAsyncInit = function() {
			    FB.init({
			      appId      : '739875756030794',
			      status     : true,
			      xfbml      : true
			    });
			    
			    FB.ui(
			  	      {
			  	       method: 'feed',
			  	       name: companyName,
			  	       caption: 'Stock Information of ' +  symbol,
			  	       description: (
			  	          'Last Trade Price: ' + lastTrade +
			  	          ', Change: ' + changeType + change
			  	          
			  	       ),
			  	       //link: 'https://developers.facebook.com/docs/reference/javascript/',
			  	       picture: imageUrl,
					   //redirect_uri: 'https://developers.facebook.com/'
			  	      },
			  	      function(response) {
			  	        if (response && response.post_id) {
			  	          alert('Post was published.');
			  	        } else {
			  	          alert('Post was not published.');
			  	        }
			  	      }
			  	    );
			  };
	  (function(d, s, id){
	     var js, fjs = d.getElementsByTagName(s)[0];
	     if (d.getElementById(id)) {return;}
	     js = d.createElement(s); js.id = id;
	     js.src = "//connect.facebook.net/en_US/all.js";
	     fjs.parentNode.insertBefore(js, fjs);
	   }(document, 'script', 'facebook-jssdk'));
		
		//document.write(company);
		
   }

	function createXMLHttpRequest() {
			if (window.XMLHttpRequest) {
				xhr = new XMLHttpRequest();
			} else {
				if (window.ActiveXObject) {
					try {
						xhr = new ActiveXObject("Microsoft.XMLHTTP");
					} catch (e) {
					}
				}
			}
	}   

   
	function loadXML(url) {

			createXMLHttpRequest();

			var queryString = "http://cs-server.usc.edu:17064/examples/servlet/GetAndPostRequest?symbol=";
			queryString = queryString + url;
			//alert(queryString);
			if (xhr) {
				xhr.open("GET", queryString, true);
				xhr.onreadystatechange = myCallBack;//
				//xhr.setRequestHeader("Connection", "Close");
				xhr
						.setRequestHeader("Method", "GET" + queryString
								+ "HTTP/1.1");
				xhr.send(null);
			} else {
				alert("Sorry, but I couldn't create an XMLHttpRequest");
			}

	}   

	function myCallBack() {
		//var tempDiv = document.createElement("div");
		//var pageDiv = document.getElementById("pictureBar");
		if (xhr.readyState == 4) {
			if (xhr.status == 200) {
				//alert(xhr.responseText);
				//var text = xhr.responseText;
				//var jsonobj = xhr.responseText.parseJSON();
				var jsonobj = eval("(" + xhr.responseText + ")");
				//var jsonResult = jsonobj.result;
				//alert(jsonobj.result.Quote.Open);
				//alert(jsonResult.Name);
				if (jsonobj.result.Quote=="Information is not available."){
					YUI().use('node',function(Y) {
						Y.one('#notFound').setHTML("<h1 align='center'><font color='#FFF'>Stock Information Not Found!</font></h1>");
						Y.one('#companyName').setAttribute("value","");//setHTML("");
						Y.one('#symbol').setHTML("");
						Y.one('#LastTradePriceOnly').setHTML("");
						Y.one('#changeType').setHTML("");
						Y.one('#change').setHTML("");
						Y.one('#share').setHTML("");
						Y.one('#SImage').setHTML("");
						Y.one('#hr').setHTML("");
						Y.one('#tabView').setHTML("");
					});
					
				}
				else{
					YUI().use('node',function(Y) {
						Y.one('#notFound').setHTML("");
						//var company = Y.one('#company'); // just the node to change
						Y.one('#companyName').setAttribute("value",jsonobj.result.Name);
						//Y.one('#companyNamename').setHTML(jsonobj.result.Name);
						Y.one('#symbol').setHTML(jsonobj.result.Name+"("+jsonobj.result.Symbol+")");
						//Y.one('#LastTradePriceOnly').setHTML("<font color='#FFF'>"+jsonobj.result.Quote.LastTradePriceOnly+" </font>");
						Y.one('#LastTradePriceOnly').setHTML(jsonobj.result.Quote.LastTradePriceOnly);
						if (jsonobj.result.Quote.ChangeType=="-"){
							Y.one('#changeType').setHTML("&nbsp;&nbsp;<image src='http://www-scf.usc.edu/~csci571/2014Spring/hw6/down_r.gif' style='width:10px;height:20px' />");
							Y.one('#change').setAttribute('color','#F00');
						}
						else{
							Y.one('#changeType').setHTML("&nbsp;&nbsp;<image src='http://www-scf.usc.edu/~csci571/2014Spring/hw6/up_g.gif' style='width:10px;height:20px' />");
							Y.one('#change').setAttribute('color','#01DF3A');
						}
						Y.one('#change').setHTML(jsonobj.result.Quote.Change+"("+jsonobj.result.Quote.ChangeInPercent+")");
						Y.one('#share').setHTML("<input type='image' src='http://selectpromomodels.com/wp-content/uploads/2013/10/facebook-logo1.png' style='width:50px;height:42px; cursor: pointer; padding-top:10px' onClick='Share()'/>");
						Y.one('#SImage').setHTML("<image id='imageUrl' src='"+jsonobj.result.StockChartImageURL+"' width=\"260\" height=\"140\" >");
					});
					//document.write("<hr>");
					if (jsonobj.result.News!="Rss feed not found"){
						var news="<ul>";
						for (var i=0;i<jsonobj.result.News.Item.length;i++){
							news+="<li><a href="+jsonobj.result.News.Item[i].Link+" target='_blank'>"+jsonobj.result.News.Item[i].Title+"</a></li>";
						}
						news +="</ul>";
					}
					else{
						var news="Financial Company News is not available";
					}
					var day = (jsonobj.result.Quote.DaysLow!="")?jsonobj.result.Quote.DaysLow+"-"+jsonobj.result.Quote.DaysHigh:"";
					var year = (jsonobj.result.Quote.YearLow!="")?jsonobj.result.Quote.YearLow+"-"+jsonobj.result.Quote.YearHigh:"";
					YUI().use('tabview', function(Y) {
						Y.one('#hr').setHTML("<hr style='text-align: center'>");
						Y.one('#tabView').setHTML("");
					    var tabview = new Y.TabView({
					        children: [{
					            label: 'Quote Information',
					            content: "<table STYLE='width:98%'><tr><td width='70' colspan=2>Prev Close:</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.PreviousClose+"</td><td colspan=2>Day's Range:</td><td width='70' colspan=2 align=right>"+day+"</td></tr>"
								+"<tr><td width='70' colspan=2>Open:</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.Open+"</td><td colspan=2>52wk Range:</td><td width='70' colspan=2 align=right>"+year+"</td></tr>"
								+"<tr><td width='70' colspan=2>Bid:</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.Bid+"</td><td colspan=2>Volume:</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.Volume+"</td></tr>"
								+"<tr><td width='70' colspan=2>Ask:</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.Ask+"</td><td colspan=2>Avg Vol(3m):</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.AverageDailyVolume+"</td></tr>"
								+"<tr><td width='70' colspan=2>1y Target Est:</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.OneYearTargetPrice+"</td><td colspan=2>Market Cap:</td><td width='70' colspan=2 align=right>"+jsonobj.result.Quote.MarketCapitalization+"</td></tr>"
								+"</table>"
					        }, {
					            label: 'News Headlines',
					            content: news
					        }]
							
							
					    });

					    tabview.render('#tabView');
					    tabview.selectChild(0);
					});
				}
			} else {
				alert("There was a problem with the request " + xhr.status);
			}
		}
	}
	
	function loadXML(str) {

		createXMLHttpRequest();

		var queryString = "http://cs-server.usc.edu:17064/examples/servlet/GetAndPostRequest?symbol=";
		queryString = queryString + str;
		//alert(queryString);
		if (xhr) {
			xhr.open("GET", queryString, true);
			xhr.onreadystatechange = myCallBack;//
			//xhr.setRequestHeader("Connection", "Close");
			xhr
					.setRequestHeader("Method", "GET" + queryString
							+ "HTTP/1.1");
			xhr.send(null);
		} else {
			alert("Sorry, but I couldn't create an XMLHttpRequest");
		}

	}
	
	//window.onload = viewXML;
	function viewXML(what) {
		//window.onload = initAll;
		var xhr = false;
		var URL = document.getElementById("input").value.split(",")[0];
		//var str = event.result.text.split(",")[0];
		//var URL = what.symbol.value;
		//	alert("Hello World!");

		if (URL.length == 0) {
			alert("Please enter company name/symbol");
		} else {
			loadXML(URL);
		}

	}
	
