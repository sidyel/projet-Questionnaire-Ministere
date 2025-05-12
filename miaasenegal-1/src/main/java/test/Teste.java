package test;

import java.util.List;

import org.dao.GenericDAO;
import org.entitee.Categorieinstitution;
import org.entitee.Employe;
import org.entitee.EmployeId;
import org.entitee.Institution;
import org.entitee.Personne;
import org.entitee.Profile;

public class Teste {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GenericDAO<Employe> dao1 = new GenericDAO<>(Employe.class);
		EmployeId id = new EmployeId();
		id.setIdemploye(18);
		id.setIdpersonne(17);
		Employe s = dao1.findById1(id);
		System.out.println(s.getPersonne().getNom());
		System.out.println(s.getPersonne().getNom());
		s.setMatricule("hhh");
		dao1.merge(s);

		 

	}

}
