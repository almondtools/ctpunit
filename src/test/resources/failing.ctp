html(content) ::= {
<html>`content`</html>
}

test1 ::= html("content").equalTo("<html>\ncontent</html>").fails()
 
test2 ::= html("othercontent").eqCW({
<html>other content</html>
}).fails()
 
test3 ::= html("other content").eqCW({
<html>othercontent</html>
}).fails()


test4 ::= html("othercontent").eqCW({
 <html>other
   content</html>
}).fails()
 
