html(content) ::= {
<html>`content`</html>
}

test1 ::= html("content").equalTo("<html>content</html>")
 
test2 ::= html("othercontent").eqCW({
 
 <html>othercontent</html>
 
})
 
test3 ::= html("other content").eqNW({
<html>
  other content
</html>
})

test4 ::= html("othercontent").eqNW({
 <html>
   other
   content
 </html>
})
