package examples.blockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import examples.blockchain.NoobChain;
import examples.blockchain.StringUtil;

public class Transaction {
	
	public String transactionId;
	public PublicKey sender; //senders address / public key
	public PublicKey recipient;	// Recipients address / public key
	public float value;	
	public byte[] signature; //signature
	
	public ArrayList<TransactionInput>  inputs  = new ArrayList<>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	private static int sequence = 0;
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}
	
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);		
	}
	
	private String calculateHash() {
		
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(
				         StringUtil.getStringFromKey(sender) +
				         StringUtil.getStringFromKey(recipient) +
				         Float.toString(value) + sequence);
	}
	
	public boolean processTransaction(){
		
		if(verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
		
		for(TransactionInput i : inputs) {
			i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
		}
		
		if(getInputsValue() < NoobChain.minimumTransaction){
			System.out.println("Transaction Inputs to small: " + getInputsValue());
			return false;
		}
		
		//generate transaction outputs:
		float leftOver = getInputsValue() - value;
		
		transactionId = calculateHash();
		outputs.add(new TransactionOutput (this.recipient, value, transactionId)); //send value to recipient
		outputs.add(new TransactionOutput (this.sender, leftOver, transactionId)); //send rest of coins back to sender
		
		//Add outputs to unspent list
		for(TransactionOutput o : outputs)	NoobChain.UTXOs.put(o.id, o);
		
		//Remove transaction inputs from UTXO lists as spent
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			NoobChain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;		
	}
	
	public float getInputsValue() {
		
		float total = 0;
		for(TransactionInput i : inputs){
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		
		return total;
	}
	
	public float getOutputsValue(){
		
		float total = 0;
		for(TransactionOutput o : outputs){
			total += o.value;
		}
		
		return total;
	}

}
