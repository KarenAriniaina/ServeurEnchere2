package Tp.model;

import Tp.dao.ObjetBDD;

public class Configuration extends ObjetBDD {

    private String idConfiguration;
    private double DureMin;
    private double DureeMax;
    private double Commission;

    public Configuration() {
        this.setNomTable("Configuration");
        this.setPrimaryKey("idConfiguration");
    }

    public String getIdConfiguration() {
        return idConfiguration;
    }

    public void setIdConfiguration(String idConfiguration) {
        this.idConfiguration = idConfiguration;
    }

    public double getDureMin() {
        return DureMin;
    }

    public void setDureMin(double dureMin) throws Exception{
        if(dureMin<0) throw new Exception("Duree min<0");
        DureMin = dureMin;
    }

    public double getDureeMax() {
        return DureeMax;
    }

    public void setDureeMax(double dureeMax) throws Exception{
        if(dureeMax<0) throw new Exception("Duree max<0");
        DureeMax = dureeMax;
    }

    public double getCommission() {
        return Commission;
    }

    public void setCommission(double commission) throws Exception{
        if(commission>100) throw new Exception("Commission>100%");
        Commission = commission;
    }

    /* 
    public void UpdateConfig() throws Exception{
        MongoDatabase m=Connexion.getMongoConnection();
        Document doc=new Document();
        if(this.getCommission()==0){
            doc.append("DureMin",this.getDureMin()).append("DureeMax", this.getDureeMax());
        }
    }

    public void getConfig() throws Exception{
        MongoDatabase database=Connexion.getMongoConnection();
        MongoCollection<Document> collection = database.getCollection("Configuration");

        // Retrieve all documents sorted by _id
        List<Document> documents = collection.find().sort(new Document("_id", 1)).into(new ArrayList<>());
        for (Document document : documents) {
            System.out.println(document.toJson());
        }
    }
    */

}
