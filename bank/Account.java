package bank;

class Account {

    // Attributes
    private String nom;
    private Integer balance;
    private Integer threshold;
    private boolean block = false;

    // Constructor
    public Account(String nom, Integer balance, Integer threshold) {
        this.nom = nom;
        this.balance = balance;
        this.threshold = threshold;
    }

    // Methods
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public String toString() {
        return this.nom + " | " + this.balance.toString() + " | " + this.threshold.toString() + " | " + this.block;
    }
}
