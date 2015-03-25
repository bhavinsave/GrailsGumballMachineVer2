import gumball.GumballMachine
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.InvalidKeyException


class GumballStatelessController {

	def String machineSerialNum = "1234998871109"
	def GumballMachine gumballMachine
	def String secretKey = "1RyyfZq3ktmCR9H5HqjLAUGxJX3g8lw5";
	def String msg
	def hash
	
	

	def hmac_sha256(String secretKey, String data) {
	 try {
		Mac mac = Mac.getInstance("HmacSHA256")
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256")
		mac.init(secretKeySpec)
		byte[] digest = mac.doFinal(data.getBytes())
		return digest
	   } catch (InvalidKeyException e) {
		throw new RuntimeException("Invalid key exception while converting to HMac SHA256")
	  }
	}
   
	
	def index() {
		
		String VCAP_SERVICES = System.getenv('VCAP_SERVICES')
		
		if (request.method == "GET") {

			
			def gumball = Gumball.findBySerialNumber( machineSerialNum )
			if ( gumball )
			{
				
				gumballMachine = new GumballMachine(gumball.modelNumber, gumball.serialNumber)
				System.out.println(gumballMachine.getAbout())
			}
			else
			{
				flash.message = "Error! Gumball Machine Not Found!"
				render(view: "index")
			}

		
			
			flash.state = gumballMachine.getCurrentState();
			
			flash.model = gumball.modelNumber ;
			flash.serial = gumball.serialNumber ;
			
			
			
			flash.ts = System.currentTimeMillis().toString();
			
			msg = flash.state + "|" + flash.model + "|" + flash.serial + "|" + flash.ts + "|" + secretKey
			hash = hmac_sha256(secretKey,msg)
			println("MSG:" + msg )
			println("HASH:" + hash.encodeBase64())
			flash.hash = hash.encodeBase64()
			
			
			
			
			flash.message = gumballMachine.getAbout()

			
			render(view: "index")
			

		}
		else if (request.method == "POST") {

			
			request.each { key, value ->
				println( "request: $key = $value")
			}

			
			params?.each { key, value ->
				println( "params: $key = $value" )
			}
			
			

			def state = params?.state
			def modelNum = params?.model
			def serialNum = params?.serial
			def tsString = params?.ts
			
			
			def long ts = Long.parseLong(tsString)
			
			def long cts = System.currentTimeMillis()
			def long diff = cts - ts
			println ( diff/1000 ) // seconds
			def hash1 = params?.hash
			def String msg = state + "|" + modelNum + "|" + serialNum + "|" + tsString + "|" + secretKey
			def hashBytes = hmac_sha256(secretKey, msg)
			def hash2 = hashBytes.encodeBase64().toString()
			println ( "MSG: " + msg )
			println ( "HASH1: " + hash1 )
			println ( "HASH2: " + hash2 )
			
			def invalidTS = ((diff/1000) > 120)
			def invalidHASH = (hash1 != hash2)
			println( "invalid ts: " + invalidTS )
			println( "invalid hash: " + invalidHASH )
			
			if( invalidTS || invalidHASH)
			{
			 
			
			flash.state = state;
			
			flash.model = modelNum ;
			flash.serial = serialNum ;
			flash.message =  "**** SESSION INVALID ***"
			}
			
	else
	{
			gumballMachine = new GumballMachine(modelNum, serialNum) ;
			gumballMachine.setCurrentState(state) ;
			
			System.out.println(gumballMachine.getAbout())
			
			if ( params?.event == "Insert Quarter" )
			{
				gumballMachine.insertCoin()
			}
			if ( params?.event == "Turn Crank" )
			{
				gumballMachine.crankHandle();
				
				if ( gumballMachine.getCurrentState().equals("gumball.CoinAcceptedState") )
				{
					def gumball = Gumball.findBySerialNumber( machineSerialNum )
					if ( gumball )
					{
						// gumball.lock() // pessimistic lock
						if ( gumball.countGumballs > 0)
							gumball.countGumballs-- ;
						gumball.save(flush: true); // default optimistic lock
					}
				}
				
			}
			
	}
			
			

		
	
		  flash.state = gumballMachine.getCurrentState();
				
				
			flash.model = modelNum ;
			flash.serial = serialNum ;
						
			flash.ts = System.currentTimeMillis().toString();
			
			msg = flash.state + "|" + flash.model + "|" + flash.serial + "|" + flash.ts + "|" + secretKey
			hash = hmac_sha256(secretKey,msg)
			//println("MSG:" + msg )
			//println("HASH:" + hash.encodeBase64())
			flash.hash = hash.encodeBase64()
			//flash.hash = hash.encodeBase64();
			// report a message to user
			flash.message = gumballMachine.getAbout()
		

			// render view
			render(view: "index")
		}
		else {
			render(view: "/error")
		}
		
	}

}