package Tp.controller;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import Tp.JSonData.JsonData;
import Tp.dao.Connexion;
import Tp.model.Admin;
import Tp.model.Categorie;
import Tp.model.ClientTop3;

@RestController
public class StatistiqueController {

    @CrossOrigin
    @GetMapping("/Statistiques")
    public JsonData getStatistique(@RequestHeader("token") String token, @RequestHeader("idAdmin") String idAdmin) throws SQLException {
        JsonData json = new JsonData();
        Connection con = null;
        Admin a = new Admin();
        a.setIdAdmin(idAdmin);
        a.setToken(token);
        if (a.VerifToken()) {
            try {
                con = Connexion.getConnection();
                Object[] liste=new Object[2];
                liste[0]=new Categorie().getCategorieTop3(con);
                liste[1]=new ClientTop3().getClientTop3(con);
                json.setData(liste);
                json.setMessage("Operation reussi");
            } catch (Exception e) {
                json.setMessage(e.getMessage());
            }   
            finally{
                if(con!=null) con.close();
            }
        } else {
            json.setData(null);
            json.setMessage("Vous n'etes pas connecté");
        }
        return json;
    }
}
