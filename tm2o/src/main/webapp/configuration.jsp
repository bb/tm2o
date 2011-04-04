<%@page import="de.topicmapslab.odata.web.ODataConfiguration"%>
<%@page import="de.topicmapslab.odata.web.IOUtis"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItem"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page
	import="org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory"%>
<%@page
	import="org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload"%>
<%@page
	import="de.topicmapslab.odata.content.remote.RemoteOdataContentProvider"%>
<%@page import="java.security.Provider"%>
<%@page
	import="de.topicmapslab.odata.config.EContentProviderConfiguration"%>
<%@page
	import="de.topicmapslab.odata.edm.EdmNavigationPropertyIdentifier"%>
<%@page import="java.util.Properties"%>
<%@page
	import="de.topicmapslab.odata.content.memory.MemoryOdataContentProvider"%>
<%@page import="de.topicmapslab.odata.TopicMapMetadata"%>
<%@page import="de.topicmapslab.odata.TopicMapODataProducer"%>
<%@page import="de.topicmapslab.odata.TopicMapODataProducerFactory"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Configuration of TM2O Service</title>
 <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
    <link href="/favicon.ico" rel="shortcut icon" />
    <link href="<%= getServletContext().getContextPath()%>/resources/css/screen.css" media="screen, projection" rel="stylesheet" type="text/css" />
    <link href="<%= getServletContext().getContextPath()%>/resources/css/print.css" media="print" rel="stylesheet" type="text/css" />
    <link href="<%= getServletContext().getContextPath()%>/resources/css/maiana.css" media="screen, projection" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	function checkServer() {
		if (document.getElementById('remPro').checked == true) {
			var url = document.getElementById('address').value;
			var http;
			if (window.XMLHttpRequest) {
				http = new window.XMLHttpRequest();
			} else {
				if (window.ActiveXObject) {
					http = new window.ActiveXObject("Microsoft.XMLHTTP");
				} else {
					throw "Cannot locate HTTP content";
				}
			}
			http.open("HEAD", url, true);
			http.onreadystatechange = function() {
				if (http.readyState == 4) {
					if (http.status == 200) {
						document.getElementById('address').style.color = '#000';
					} else {
						document.getElementById('address').style.color = '#F00';
					}
				}
			}
			http.send(null);
		}
	}
</script>
</head>
<body class="maintenance">
    <div id="wrapper">
      <div id="header">
        <div class="container">
          <div class="span-20 prepend-2 append-2 last">
          	<img src="<%=getServletContext().getContextPath()%>/resources/images/odata.png" style="position:absolute;left:5px;top:5px;"/>
            <div id="logo">
              <h1>TM2O-Service Configuration</h1>
            </div>
          </div>
        </div>
      </div>      	
	    <div id="content">&nbsp;
	    <h2>Configuration</h2>
      	<br />
      	Please add something like: If you need help for the configuration please read the <a href="http://code.google.com/p/tm2o/wiki/Configuration"> here</a>.
      	<br />
      	<hr />
			<%
				TopicMapODataProducer producer = TopicMapODataProducerFactory.getProducerInstance();
				TopicMapMetadata mt = producer.getService();
				boolean local = false;
				String namespace = null;
				Properties prop = null;
				String file = null;
				String url = null;
				boolean flatAssociation = false;
				/*
				 * is get request	
				 */
				if (request.getMethod().equalsIgnoreCase("GET")) {
					local = MemoryOdataContentProvider.class.isAssignableFrom(mt.getContentProviderClass());
					namespace = mt.getNamespace();
					prop = mt.getProperties();
					file = "";
					url = !local ? prop.get("server").toString() : "";
					flatAssociation = prop.get("association-mode").toString().equalsIgnoreCase(EContentProviderConfiguration.FLAT_ASSOCIATION.name());
				}
				/*
				 * is post
				 */
				else if (request.getMethod().equalsIgnoreCase("POST")) {
					/*
					 *
					 */
					ODataConfiguration cfg = IOUtis.post(request);
					/*
					 * update ODATA service
					 */
					mt.reload(cfg.getClazz(), cfg.getProperties(), cfg.getNamespace());
					TopicMapODataProducerFactory.writeProperties(cfg.getClazz(), cfg.getProperties(), cfg.getNamespace());
					/*
					 * set outside values
					 */
					local = MemoryOdataContentProvider.class.isAssignableFrom(cfg.getClazz());
					namespace = cfg.getNamespace();
					prop = cfg.getProperties();
					file = "";
					url = !local ? prop.get("server").toString() : "";
					flatAssociation = prop.get("association-mode").toString().equalsIgnoreCase(EContentProviderConfiguration.FLAT_ASSOCIATION.name());
				}
			%>
			<form action="configuration.jsp" method="POST"
				enctype="multipart/form-data">
			<table>
				<tr>
					<td><b>Namespace:</b></td>
					<td><input name="namespace" type="text" value="<%=namespace%> "
						size="50"></td>
				</tr>
				<tr>
					<td><b>Association Mode:</b></td>
					<td><input name="flat" id="flat" type="radio"
						<%=flatAssociation ? "checked=\"checked\"" : ""%>
						onclick="document.getElementById('strong').checked = false;">
					FLAT <input name="strong" id="strong" type="radio"
						<%=flatAssociation ? "" : "checked=\"checked\""%>
						onclick="document.getElementById('flat').checked = false;">
					STRONG</td>
				</tr>
				<tr>
					<td><b>Local Content Provider:</b></td>
					<td><input name="locPro" type="radio" id="locPro"
						<%=local ? "checked=\"checked\"" : ""%>
						onclick="document.getElementById('remPro').checked = false;"></td>
				</tr>
				<tr>
					<td><b>File:</b></td>
					<td><input name="file" value="<%=file%>" id="file" name="Datei"
						type="file" size="50" maxlength="100000"></td>
				</tr>
				<tr>
					<td><b>Remote Content Provider:</b></td>
					<td><input name="remote" type="radio" id="remPro"
						<%=!local ? "checked=\"checked\"" : ""%>
						onclick="document.getElementById('locPro').checked = false;"><br />
					</td>
				</tr>
				<tr>
					<td><b>MaJorToM Server Address:</b></td>
					<td><input name="address" id="address" type="text"
						value="<%=url%>" size="50" onblur="checkServer()"></td>
				</tr>
			</table>
			<input type="submit" value="Set"></form>    
	    </div>	    
	</div>
    <div id="footer">
      <div class="container">
        <div class="span-20 prepend-2">
          <ul class="horizontal">
			<li class="noBullet"><a href="http://code.google.com/p/tm2o/wiki/Main">Documentation</a></li>
            <li class="noBullet"><a href="http://topicmapslab.de/impressum">Imprint</a></li>
            <li>TM2O-Service is a service by the <a href="http://www.topicmapslab.de">Topic Maps Lab</a> &copy; 2011</li>
          </ul>
        </div>
      </div>
    </div>
    <div>
    <a id="sideLabelLink" href="http://www.topicmapslab.de/"><span id="sideLabel">
     &nbsp;</span></a>
     </div> 
<div><a id="sideLabelLink" href="http://www.topicmapslab.de/"><span
	id="sideLabel"> &nbsp;</span></a></div>
</body>
</html>