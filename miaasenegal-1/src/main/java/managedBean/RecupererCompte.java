package managedBean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.dao.GenericDAO;
import org.entitee.Employe;
import org.entitee.Personne;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import utilitaires.StringGenerator;

@Named
@ViewScoped
public class RecupererCompte implements Serializable
{
	
	private String recup;
	private String newMotDePAsse;

	
	GenericDAO<Employe> daoEmploye=new GenericDAO<>(Employe.class);
	GenericDAO<Personne> daoPersonne=new GenericDAO<>(Personne.class);

	Employe employe=new Employe();
	
	public void recuperer()
	{
		if(recup != null)
		{
			employe=daoEmploye.recupererEmploye(recup);
			if(employe != null)
			{
				
				System.out.println(employe.getPersonne().getNom());
				newMotDePAsse=StringGenerator.genererChaine();
				System.out.println(newMotDePAsse);
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	            employe.getPersonne().setMotpasse(passwordEncoder.encode(newMotDePAsse));
	            daoPersonne.merge(employe.getPersonne());

	            daoEmploye.merge(employe);
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employé mis à jour"));

			}
			else
			{
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("le login ou l'email est incorrecte"));

			}
			
			
		}
		
	}

	public String getRecup() {
		return recup;
	}

	public void setRecup(String recup) {
		this.recup = recup;
	}

	public Employe getEmploye() {
		return employe;
	}

	public void setEmploye(Employe employe) {
		this.employe = employe;
	}

	public String getNewMotDePAsse() {
		return newMotDePAsse;
	}

	public void setNewMotDePAsse(String newMotDePAsse) {
		this.newMotDePAsse = newMotDePAsse;
	}

}
