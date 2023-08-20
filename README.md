# Curl Import
# Extracting a Curl from any file extension of the 3 (TEXT,YAML, JSON) and there are 3 classes and 3 files for every file type 
**After importing the file with the inputs 
**
1-ClassName
2-Request Spec
3-Curl 
4-Expected status code 
5-Output path of the file   

# There are 2 files generated afterward 
  -Test Class holding (URL, METHOD, HEADERS) 
  -Payload Class holding (BODY)

curl is then extracted into 4 parts 

-S --------URL

-X -------- Method 

-H -------- Headers 

-D--------- Body 


Then using a Writer Constructor there are 2 classes generated 

1-Payload class that holds the body extracted from the curl in this format
```
public class ExamplePayload
{
    public static String examplePayload()
    {
        return "Here is the body extracted "
}
```
2-Test class that holds the URL, Method, and Headers extracted from the curl in this format
```
public class CurlRequest.ExampleTest
{
    @Test
    public static void exampleTest() throws IOException
    {
        given().
                spec("here are the headers extracted").
        when().
                "here is the method extracted"("here is the url extracted").
        then().
                statusCode(200);
    }
}
in case of request spec sent then the headers are not generated 
in case of sending the output - then a new package will be generated 
```
