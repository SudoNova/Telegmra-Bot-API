import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by HellScre4m on 5/18/2016.
 */
public class ViewMember
{
	public static void main (String[] args) throws Exception
	{
		String content = "";
		Scanner in = new Scanner(System.in);
		String phoneNumber = in.next();
		int rep = in.nextInt();
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost req = new HttpPost("http://5.9.232.163/Telemember3/seke_raygan_ruz.php");
		HttpResponse response;
		ArrayList<NameValuePair> list = new ArrayList<>(1);
		list.add(new BasicNameValuePair("phone", phoneNumber));
		HttpEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
		req.setEntity(entity);
		for (int i = 0; i < rep; i++)
		{
			response = client.execute(req);
			content = EntityUtils.toString(response.getEntity());
			System.out.println(content);
		}
	}
	
	
}
