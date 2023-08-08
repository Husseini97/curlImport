# curlImport
There is a JSON File all that to do is copy and paste the curl you want in the JSON file and then run the main method.

the curl is then extracted into 4 parts 

-S --------URL

-X -------- Method 

-H -------- Headers 

-D--------- Body 


Then using a Writer Constructor there are 2 classes generated 
1- A Payload class that holds the body extracted from the curl in this format
```
public class examplePayload
{
    public static String dispatchLtsLeadPayload()
    {
        return "Here is the body extracted "
}
```
2- A Test class that holds the URL, Method, and Headers extracted from the curl in this format
```
public class ExampleTest
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
```
