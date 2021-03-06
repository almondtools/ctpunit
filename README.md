# CtpUnit

CtpUnit is a Unit Test Framework for [ComTemplate](https://github.com/almondtools/comtemplate).

## Starting with CtpUnit

A typical test specification may be following file `testexample.ctg`:

    html(content) ::= {
      <html>@content</html>
    }

    testSuccess ::= html("content").eqCW("<html>content</html>")
    
    testFails ::= html("content").equalTo("<html>content</html>")

The TestRunner will collect and execute every constant definition starting with "test". For this we need a companion file in java:

    @RunWith(CtpUnitRunner.class)
    @Spec
    public class TestExample {
      
      @Spec(group="testexample")
      public void testexample() {
      }
      
    }

Note that this companion file does contain only configuration, it is not meant to contain more. Since the contents are highly redundant it is planned to eliminate this companion file and replace it by a more generic or at least simpler concept.

## Matchers

CtpUnit provides some Standard Matchers for testing:

### equalTo

The `equalTo` matcher returns **success** if the base and the argument are literally equal. In general this is whitespace-sensitive, yet the first and last newline get cut.   

### contains

The `contains` matcher returns **success** if the base string contains the argument (or elements of the argument list):
- if the argument is a string value base must contain the given argument
- if the argument is a list base must contain the elements of this list in correct order.

This matcher is whitespace sensitive an case sensitive on the arguments value(s).

### eqNW

The `eqNW` matcher returns **success** if the base and the argument are equal ignoring whitespace.

### eqCW

The `eqCW` matcher returns **success** if the base and the argument are equal with compressed whitespace:

- inner whitespace sequences are reduces to `' '` (single space)
- leading and trailing whitespace sequences are trimmed (replaced by empty string)

### fails

The `fails` matcher return **success** if the base returns **failure**, it returns **failure** if the base returns **success**. Ignored Tests or Errors are not affected by this matcher.

## Custom Matchers

A developer can write custom Matchers by extending the class `FunctionMatcher` and adding the matcher to the test companion file:

    @RunWith(CtpUnitRunner.class)
    @Spec
    @Matcher(CustomerMatcher.class)
    public class TestExample {
      ...
    }

## Planned Features

- more matchers


