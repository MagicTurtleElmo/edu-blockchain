package examples.blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

import examples.blockchain.transaction.Transaction;
import examples.blockchain.transaction.TransactionOutput;

public class NoobChain {
	
	public static int difficulty = 6;
	
	public static ArrayList<Block> blockchain = new ArrayList<>(); 
	public static HashMap<String,TransactionOutput> UTOWx = new HashMap<>();
	
	public static Wallet walletA;
	public static Wallet walletB;
	
	public static void main(String[] args){
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		walletA = new Wallet();
		walletB = new Wallet();
		
		System.out.println("Private and public keys:");
		
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
				
		System.out.println("Is signature verified");
		System.out.println(transaction.verifySignature());
		
	}
	
	public static Boolean isChainValid() {
		Block currentBlock, previousBlock;
		
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		for(int i=1; i < blockchain.size(); i++){
			currentBlock  = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			if(!currentBlock.hash.equals(currentBlock.calculateHash())){ 
				System.out.println("Current Hashes not equal");
				return false;
			}
			
			if(!previousBlock.hash.equals(currentBlock.previousHash)){
				System.out.println("revious Hashes not equal");
				return false;
			}
			
			if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)){
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		
		return true;
	}

}
