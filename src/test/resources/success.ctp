html(content) ::= {
<html>@content</html>
}

test1 ::= html("content").equalTo("<html>content</html>")
 
test2 ::= html("othercontent").evaluatesTo({
 <html>
   othercontent
 </html>
}
 
test3 ::= html("other content").evaluatesTo({
<html>
  other content
</html>
}