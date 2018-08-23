<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
    <ul>
    <#list fileList as file>
   	<li> <a href="${dir}/${file}">${file}<br/></a></li>
	</#list>
	</ul>
</body>
</html>