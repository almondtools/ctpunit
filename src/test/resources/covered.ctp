html(content) ::= {
<<if(content.empty(),"empty",{<html><<content>></html>})>>
}

test1 ::= html("content").equalTo("<html>content</html>")

test2 ::= html("").equalTo("empty")
