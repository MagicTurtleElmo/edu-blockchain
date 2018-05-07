package examples.blockchain.transaction;

public class TransactionInput {
	
    /**
     * Reference to tranaction outputs -> transaction Id
     */
	public String transactionOutputId;
	
	/**
	 * Contains the Unspent transaction output
	 */
	public TransactionOutput UTXO;
	
	public TransactionInput(String transactionOutputId){
		this.transactionOutputId = transactionOutputId;
	}

}
