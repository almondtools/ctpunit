# CtpUnit

CtpUnit is a Unit Test Framework for [ComTemplate](https://github.com/almondtools/comtemplate).

## Starting with CtpUnit

A typical test specification may be following file `testexample.ctg`:

    html(content) ::= {
      <html>@content</html>
    }

    testSuccess ::= html("content").evaluatesTo("<html>content</html>")
    
    testFails ::= html("content").equalTo("<html>content</html>")

The TestRunner will collect and execute every constant definition starting with "test". For this we need a companion file in java:

    @RunWith(CtpUnitRunner.class)
    @Spec
    public class TestSuccessFailing {
      
      @Spec(group="testexample")
      public void testexample() {
      }
      
    }

Note that this companion file does contain only configuration, it is not meant to contain more. Since the contents are highly redundant it is planned to eliminate this companion file and replace it by a more generic or at least simpler concept.

## Planned Features


