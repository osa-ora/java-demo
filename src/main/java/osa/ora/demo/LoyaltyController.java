package osa.ora.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

@RestController
@RequestMapping("/loyalty/v1")
public class LoyaltyController {
	
	/**
	 * Rest Service to return the loyalty account balance
	 * @param account id
	 * @return the account balance
	 */
	@GetMapping("/balance/{account}")
	public String getBalance(@PathVariable(value = "account") Integer account) {
		System.out.println("Get Balance for account: "+account);
		Random random = new Random();
        	int balance = random.nextInt(5001);
		String results="{\"account\":"+account+ ",\"balance\": "+balance+"}";
		return results;
	}
	/**
	 * Rest Service to return the last transaction for an account
	 * @param account id
	 * @return the last transaction
	 */
	@GetMapping("/transaction/{account}")
	public String getLastTransaction(@PathVariable(value = "account") Integer account) {
		System.out.println("Get Last Transactions for account: "+account);
		Random random = new Random();
        	int transaction = random.nextInt(1000);
		int trans_id = random.nextInt(12345)+765544;
		String results="{\"transaction id\": "+trans_id+",\"account\":"+account+",\"value\": "+transaction+",\"POS\": \"Pizza Shop\",\"description\": \"Pizza Purchase\"}";
		return results;
	}

}
