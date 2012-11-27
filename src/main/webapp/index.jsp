<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>IEHR hData hStore</title>
        
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.8.2.js"></script>
        <script src="http://code.jquery.com/ui/1.9.1/jquery-ui.js"></script>
        <!--<link rel="stylesheet" href="/resources/demos/style.css" />-->
        <script>
        $(function() {
            $( "input[type=submit], a, button" )
                .button()
                .click(function( event ) {
                    event.preventDefault();
                });
        });
        </script>
    </head>
    
    <body>

        <h1>hData Load</h1>
        
        <input type="submit" value="A submit button" />

        <p>
        
        <h1>hData hStore</h1>
        
        <p><a href="webresources/12345">Root Atom feed</a>
        <p><a href="webresources/12345/root.xml">root.xml</a>

    </body>
</html>
