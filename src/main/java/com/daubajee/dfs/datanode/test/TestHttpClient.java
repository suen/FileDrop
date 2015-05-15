package com.daubajee.dfs.datanode.test;

import static java.nio.file.StandardOpenOption.CREATE;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpScheme;

public class TestHttpClient {
	
	
	public static void testFileUpload() throws Exception
    {
        // Prepare a big file to upload
      
        Path upload = Paths.get("./testdata/afile.txt");
        try (OutputStream output = Files.newOutputStream(upload, CREATE))
        {
            byte[] kb = new byte[1024];
            for (int i = 0; i < 10 * 1024; ++i)
                output.write(kb);
        }

		HttpClient client = new HttpClient();
		// Configure HttpClient here
		client.start();
		
        final AtomicLong requestTime = new AtomicLong();
        ContentResponse response = client.newRequest("localhost", 8080)
                .scheme(HttpScheme.HTTP.toString())
                .file(upload)
                .onRequestSuccess(new Request.SuccessListener()
                {
                    @Override
                    public void onSuccess(Request request)
                    {
                        requestTime.set(System.nanoTime());
                    }
                })
                .timeout(90, TimeUnit.SECONDS)
                .send();
        long responseTime = System.nanoTime();

        System.out.println(response.getStatus());
        
        System.out.println(response.getContentAsString());
        
//        Assert.assertEquals(200, response.getStatus());
 //       Assert.assertTrue(requestTime.get() <= responseTime);

        // Give some time to the server to consume the request content
        // This is just to avoid exception traces in the test output
   //     Thread.sleep(1000);
    }
	
	public static void main(String[] args) throws Exception {
		testFileUpload();
		
	}
}
