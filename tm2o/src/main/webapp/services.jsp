<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="de.topicmapslab.odata.web.IOUtis"%>
<%@page import="de.topicmapslab.odata.content.memory.MemoryOdataContentProvider"%>
<%@page import="de.topicmapslab.odata.TopicMapODataProducerFactory"%>
<%@page import="de.topicmapslab.odata.TopicMapODataProducer"%>
<%@page import="de.topicmapslab.odata.TopicMapMetadata"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="/favicon.ico" rel="shortcut icon" />
<link
	href="<%=getServletContext().getContextPath()%>/resources/css/screen.css"
	media="screen, projection" rel="stylesheet" type="text/css" />
<link
	href="<%=getServletContext().getContextPath()%>/resources/css/print.css"
	media="print" rel="stylesheet" type="text/css" />
<link
	href="<%=getServletContext().getContextPath()%>/resources/css/maiana.css"
	media="screen, projection" rel="stylesheet" type="text/css" />
<title>OData services</title>
</head>
<body class="maintenance">
<div id="wrapper">
<div id="header">
<div class="container">
<div class="span-20 prepend-2 append-2 last">
<img src="<%=getServletContext().getContextPath()%>/resources/images/odata.png" style="position:absolute;left:5px;top:5px;"/>
<div id="logo"></div>	
</div>
</div>
</div>
<div id="content">&nbsp;
<h2>TM2O</h2>
This services exposes Topic Maps data as <a href="http://www.odata.org">Odata</a>. The Open Data Protocol (OData) is a Web protocol for querying (and updating) data that provides a way to unlock the data and free it from silos that exist in applications today. 
Usually the topic maps exposed by this service as OData are managed by an external <a href="http://code.google.com/p/majortom-server/">MaJorToM Server</a> , but can also be stored locally in this service. You will find a list of OData consumers <a href="http://www.odata.org/consumers">here.</a>
<br />
<h2>Available Services</h2>
<%
	TopicMapODataProducer producer = TopicMapODataProducerFactory.getProducerInstance();
	TopicMapMetadata mt = producer.getService();
	boolean local = MemoryOdataContentProvider.class.isAssignableFrom(mt.getContentProviderClass());
	
	if ( local ){
%>
	<b><a href="<%=getServletContext().getContextPath() + "/odata.svc/local" %>">Local OData Service<%=mt.isLoadedService("local")?"(loaded)":"(not loaded)" %></a></b>
<%
	}else{
		Object serverAddress = mt.getProperties().get("server");
		Object apiKey = mt.getProperties().get("api-key");
		if ( serverAddress != null && apiKey != null  ){
			Map<String, String> tms = IOUtis.getTopicMapIds(serverAddress.toString(),apiKey.toString());
			if ( tms == null ){
%>
		<b>The MaJorToM server does not response to any request.</b>
<%
			}else if ( tms.isEmpty()){
				%>
			<b>No topic maps created yet, go to <a href="<%= serverAddress.toString() + "/admin/newtopicmap"%>">administration interface</a> to upload a new file!</b>
				<%
			}else{
				for ( Entry<String, String> tm : tms.entrySet()){
					String topicMapId = tm.getValue();
					String topicMapLocator = tm.getKey();
					String url = getServletContext().getContextPath() + "/odata.svc/" + topicMapId;
%>
		<b><a href="<%=url %>">OData Service of &quot;<%= topicMapLocator %>&quot; <%=mt.isLoadedService(topicMapId)?"(loaded)":"(not loaded)" %></a></b><br />
<%
				}
%>
		<br />
		<b>Go to <a href="<%= serverAddress.toString() + "/admin/newtopicmap"%>">administration interface</a> to upload a new topic map!</b>
<%

			}
		}else{
%>
		<b>Missing <%=serverAddress==null?"server address":"API key"%> for remote content provider!</b>
<%
		}

	}
%>
<br />
<h2>TM2O Configuration</h2>
You can configure your TM2O service <a href="<%= getServletContext().getContextPath() + "/configuration.jsp"%>"> here</a><br />
</div>
<div id="footer">
<div class="container">
<div class="span-20 prepend-2">
<ul class="horizontal">
	<li class="noBullet"><a href="http://code.google.com/p/tm2o/wiki/Main">Documentation</a></li>
	<li class="noBullet"><a href="http://topicmapslab.de/impressum">Imprint</a></li>
	<li>TM2O-Service is a service by the <a
		href="http://www.topicmapslab.de">Topic Maps Lab</a> &copy; 2011</li>
</ul>
</div>
</div>
</div>
</div>
</body>
</html>