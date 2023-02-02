package Tp.model;

import Tp.dao.Connexion;
import Tp.dao.ObjetBDD;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Client extends ObjetBDD {
    private String idClient;
    private String Nom;
    private String Email;
    private String Mdp;
    private double Solde;

    public Client() {
        this.setNomTable("Client");
        this.setPrimaryKey("idClient");
    }

    private String Token;

    public String getToken() {
        if (Token == null)
            this.setToken(this.getJWTToken());
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) throws Exception{
        if(nom.equalsIgnoreCase("")) throw new Exception("Nom vide");
        Nom = nom;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) throws Exception{
        if(email.equalsIgnoreCase("")) throw new Exception("Email vide");
        Email = email;
    }

    public String getMdp() {
        return Mdp;
    }

    public void setMdp(String mdp) throws Exception{
        if(mdp.equalsIgnoreCase("")) throw new Exception("Mot de passe vide");
        Mdp = mdp;
    }

    public double getSolde() {
        return Solde;
    }

    public void setSolde(double solde) {
        Solde = solde;
    }

    public String getJWTToken() {
        Instant now = Instant.now();
        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());
                String jwtToken = Jwts.builder()
                .claim("idAdmin", this.getIdClient())
                .setSubject(this.getIdClient())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(2, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS256,hmacKey)
                .compact();
        return "Bearer " + jwtToken;
    }

    public boolean VerifToken() {
        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());
        Jws<Claims> jwt =null;
        try{
            jwt= Jwts.parser()
                .setSigningKey(hmacKey)
                .parseClaimsJws(this.getToken().replaceFirst("Bearer ", ""));
        }catch(io.jsonwebtoken.ExpiredJwtException ex){
            jwt=null;
        }
        if(jwt!=null) return true;
        return false;
    }

    public void EncrypterMdp() throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(this.getMdp().getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        this.setMdp(sb.toString());
    }

    public Object[] getListeEnchere() throws Exception{
        MongoDatabase database = Connexion.getMongoConnection();
        MongoCollection<Document> collection = database.getCollection("Enchere");
        Enchere[] liste=null;
        ArrayList<Enchere> le=new ArrayList<>();
        for (Document doc : collection.find().filter(new Document("idClient",this.getIdClient()))) {
            Enchere e=new Enchere();
            e.setDate(Timestamp.valueOf(doc.getString("datefin")));
            e.setIdEnchere(doc.getString("idEnchere"));
            e.setNom(doc.getString("Nom"));
            e.setIdCategorie(doc.getString("idCategorie"));
            e.setPrixDepart(doc.getDouble("prixdepart"));
            e.setCategorie(doc.getString("Categorie"));
            e.setDescription(doc.getString("description"));
            e.setEncherir((List<Encherir>)doc.get("encherir"));
            e.setPhotos((List<String>) doc.get("photos"));
            le.add(e);
        }
        return le.toArray();
    }

}
