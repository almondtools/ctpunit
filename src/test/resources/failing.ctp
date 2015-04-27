html(content) ::= {
<html>@content</html>
}

test1 ::= html("content").equalTo("<html>\ncontent</html>").fails()
 
test2 ::= html("othercontent").evaluatesTo({
<html>
  other content
</html>
}).fails()
 
test3 ::= html("other content").evaluatesTo({
<html>
  othercontent
</html>
}).fails()
