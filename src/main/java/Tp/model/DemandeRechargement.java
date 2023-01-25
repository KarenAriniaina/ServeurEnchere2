package Tp.model;

import Tp.dao.ObjetBDD;

public class DemandeRechargement extends ObjetBDD {
    private String idDemandeRechargement;
    private String idClient;
    private String nom;
    private double montant;
    private int statut; // 1: en cours 2:Valide

    public DemandeRechargement() {
        this.setNomTable("DemandeRechargement");
        this.setPrimaryKey("idDemandeRechargement");
    }

    public String getIdDemandeRechargement() {
        return idDemandeRechargement;
    }

    public void setIdDemandeRechargement(String idDemandeRechargement) {
        this.idDemandeRechargement = idDemandeRechargement;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) throws Exception {
        if (montant <= 0)
            throw new Exception("Montant rechargement inferieur à 0");
        this.montant = montant;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

}