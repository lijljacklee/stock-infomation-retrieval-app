<?php 
	$symbol = $_GET["symbol"];
	$url = "http://query.yahooapis.com/v1/public/yql?q=Select%20Name%2C%20Symbol%2C%20LastTradePriceOnly%2C%20Change%2C%20ChangeinPercent%2C%20PreviousClose%2C%20DaysLow%2C%20DaysHigh%2C%20Open%2C%20YearLow%2C%20YearHigh%2C%20Bid%2C%20Ask%2C%20AverageDailyVolume%2C%20OneyrTargetPrice%2C%20MarketCapitalization%2C%20Volume%2C%20Open%2C%20YearLow%20from%20yahoo.finance.quotes%20where%20symbol%3D%22".$symbol."%22&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	$xml   =   simplexml_load_file(urlencode($url));
	header("Content-type: text/xml; charset=utf-8");
	//echo "***************";
	//echo $xml->asXml();
	//echo $xml->getName();
	//echo $xml->results->quote->Volume;
	//echo $xml->results->quote->Symbol;
	//echo $xml->results->quote->Name;
	if ($xml->getName()=="query" && $xml->results->quote->Volume!="" && $xml->results->quote->Symbol!="") {
						$results = $xml->results;
		$quote = $results->quote;
		
		//header("Content-type: text/html; charset=utf-8");   
		//displayHeadlines($quote->Symbol); 
		$newXml=new SimpleXMLElement('<?xml version="1.0" encoding="utf-8"?><result />');  
		$newXml->addchild("Name",htmlspecialchars($quote->Name, ENT_QUOTES));  
		$newXml->addchild("Symbol",htmlspecialchars($quote->Symbol, ENT_QUOTES));
		$quote2 = $newXml->addchild("Quote");
		if ($quote->Change < 0){
			$quote2->addchild("ChangeType","-");
		}
		else if ($quote->Change!=0){
			$quote2->addchild("ChangeType","+");
		}
		else {
			$quote2->addchild("ChangeType","");
		}
		$quote2->addchild("Change",(float)substr($quote->Change,1));
		$quote2->addchild("ChangeInPercent",(float)substr($quote->ChangeinPercent,1)."%");
		$quote2->addchild("LastTradePriceOnly",number_format((float)$quote->LastTradePriceOnly,2));
		$quote2->addchild("PreviousClose",number_format((float)$quote->PreviousClose,2));
		$quote2->addchild("DaysLow",number_format((float)$quote->DaysLow,2));
		$quote2->addchild("DaysHigh",number_format((float)$quote->DaysHigh,2));
		$quote2->addchild("Open",number_format((float)$quote->Open,2));
		$quote2->addchild("YearLow",number_format((float)$quote->YearLow,2));
		$quote2->addchild("YearHigh",number_format((float)$quote->YearHigh,2));
		$quote2->addchild("Bid",number_format((float)$quote->Bid,2));
		$quote2->addchild("Volume",number_format((int)$quote->Volume));
		$quote2->addchild("Ask",number_format((float)$quote->Ask,2));
		$quote2->addchild("AverageDailyVolume",number_format((int)$quote->AverageDailyVolume));
		$quote2->addchild("OneYearTargetPrice",number_format((float)$quote->OneyrTargetPrice,2));
		$quote2->addchild("MarketCapitalization",$quote->MarketCapitalization);
		
		
		$url2 = "http://feeds.finance.yahoo.com/rss/2.0/headline?s=".$symbol."&region=US&lang=en-US";
		$xml2   =   simplexml_load_file(urlencode($url2));
		//echo $xml2->getName()."<br>";
		$title = $xml2->channel->item[0]->title;
		if ($title=="Yahoo! Finance: RSS feed not found"){
			$newXml->addchild("News","Rss feed not found");
			//echo "<center><h2>Financial Company News is not available</h2></center>";
		}
		else {
			$news = $newXml->addchild("News");
			$items = $xml2->channel->item;
			$num = 0;
			$item1;
			foreach ($items as $item){
				//echo $num++;
				$item1 = $news->addchild("Item");
				
				//$title = $item->title;
				//echo $title;
				//$title = htmlentities($title, ENT_QUOTES, 'UTF-8');
				
				$child = $item1->addchild("Title");
				$item1->Title= preg_replace("/<a[^>]*href=[^>]*>|<\/[^a]*a[^>]*>/i","",$item->title);
				
				//$item1->addchild("Title",$item->title);
				
				$link = $item->link;
				$link = htmlentities($link, ENT_XHTML, 'UTF-8');// ENT_QUOTES
				$item1->addchild("Link",$link);
				
				//echo $item1->asXml()."<br>~~~~~~";
				//echo $item1->Title."<br>";
				//echo $item->title."<br>";
				//echo $link."<br>";
			}
		}
		$imageURL = "http://chart.finance.yahoo.com/t?s=".$symbol."&lang=enUS&amp;width=300&height=180";
		$imageURL = htmlentities($imageURL, ENT_QUOTES, 'UTF-8');
		$newXml->addchild("StockChartImageURL",$imageURL);
		
		//echo "++++";
		//echo $xml2->asXml();
		//echo $xml2->channel->title."<br>";
		//echo "----";
		//Header('Content-type: text/xml');
	    //Header('Charset=UTF-8');
		//echo ("<pre>".htmlspecialchars($newXml->asXml())."</pre>");
		echo ($newXml->asXml());
	}
	else{
			//header("Content-type: text/html; charset=utf-8"); 
			$newXml=new SimpleXMLElement('<?xml version="1.0" encoding="utf-8"?><result />');
			$newXml->addchild("Quote","Information is not available.");
			echo $newXml->asXml();//echo "<center><h2>Stock Information Not Available</h2></center>";
	}
?> 


