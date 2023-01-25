package Tp.controller;

import Tp.JSonData.JsonData;
import Tp.model.Admin;
import Tp.model.Client;
import Tp.dao.ObjetBDD;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilisateurController {

    @CrossOrigin
    @PostMapping("/LoginAdmin")
    public JsonData LoginAdmin(@RequestParam String login, @RequestParam String mdp) throws Exception {
        JsonData json = new JsonData();
        try {
            Admin a = new Admin();
            a.setLogin(login);
            a.setMdp(mdp);
            a.EncrypterMdp();
            ObjetBDD[] list = a.Find(null);
            Admin[] la = new Admin[list.length];
            System.arraycopy(list, 0, la, 0, la.length);
            if (la.length != 0) {
                if (la[0] != null) {
                    // --- tsy maintsy atsoina satria tsy auto setter ao amn re ObjetBDD re
                    // token---//
                    Admin ad = la[0];
                    ad.getToken();
                    la[0] = ad;
                    json.setData(la);
                    json.setMessage("Operation reussie");
                    json.setStatus(true);

                } else {
                    json.setData(null);
                    json.setMessage("failed to log");
                    json.setStatus(false);
                }
            }
        } catch (Exception e) {
            json.setData(null);
            json.setMessage("Operation echoue");
            json.setStatus(false);
            json.setErreur(e.getMessage());
        }
        return json;

    }

    @CrossOrigin
    @PostMapping("/InscriptionAdmin")
    public JsonData InscriptionAdmin(@RequestParam String login, @RequestParam String mdp) throws Exception {
        JsonData json = new JsonData();
        try {
            Admin a = new Admin();
            a.setLogin(login);
            a.setMdp(mdp);
            a.EncrypterMdp();
            a.Create(null);
            json.setData(null);
            json.setMessage("Operation reussi");
            json.setStatus(true);
        } catch (Exception e) {
            json.setData(null);
            json.setMessage("Operation echoue");
            json.setStatus(false);
            json.setErreur(e.getMessage());
        }
        return json;

    }

    @CrossOrigin
    @PostMapping("/LoginClient")
    public JsonData LoginClient(@RequestParam String email, @RequestParam String mdp) throws Exception {
        JsonData json = new JsonData();
        try {
            Client c = new Client();
            c.setEmail(email);
            c.setMdp(mdp);
            c.EncrypterMdp();
            ObjetBDD[] list = c.Find(null);
            Client[] lc = new Client[list.length];
            System.arraycopy(list, 0, lc, 0, lc.length);
            if (lc.length != 0) {
                if (lc[0] != null) {
                    // --- tsy maintsy atsoina satria tsy auto setter ao amn re ObjetBDD re
                    // token---//
                    Client ad = lc[0];
                    ad.getToken();
                    lc[0] = ad;
                    json.setData(lc);
                    json.setMessage("Operation reussie");
                    json.setStatus(true);

                } else {
                    json.setData(null);
                    json.setMessage("failed to log");
                    json.setStatus(false);
                }
            } else {
                json.setMessage("failed to log");
                json.setStatus(false);
            }
        } catch (Exception e) {
            json.setData(null);
            json.setMessage("Operation echoue");
            json.setStatus(false);
            json.setErreur(e.getMessage());
        }
        return json;

    }

    @CrossOrigin
    @PostMapping("/InscriptionClient")
    public JsonData InscriptionClient(@RequestParam("nom") String nom, @RequestParam("email") String email,
            @RequestParam("mdp") String mdp) throws Exception {
        JsonData json = new JsonData();
        try {
            Client c = new Client();
            c.setEmail(email);
            c.setMdp(mdp);
            c.setNom(nom);
            c.EncrypterMdp();
            c.Create(null);
            // c.setIdClient("Client_"+Integer.toString(c.currentSequence(null)));
            Object[] lc = new Object[1];
            lc[0] = c;
            json.setData(lc);
            json.setMessage("Operation Reussi");
        } catch (Exception e) {
            json.setData(null);
            json.setMessage("Operation echoue");
            json.setStatus(false);
            json.setErreur(e.getMessage());
        }
        return json;

    }
}
