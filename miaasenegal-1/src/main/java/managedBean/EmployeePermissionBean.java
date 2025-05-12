package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.dao.GenericDAO;
import org.entitee.Personne;
import org.entitee.Permission;
import org.entitee.Rolepermission;
import org.entitee.RolepermissionId;
import java.time.LocalDate;

@Named("employeePermissionBean")
@ViewScoped
public class EmployeePermissionBean implements Serializable {

    private List<EmployeePermissionWrapper> employeePermissions = new ArrayList<>();
    private List<Permission> allPermissions = new ArrayList<>();

    private GenericDAO<Personne> daoPersonne = new GenericDAO<>(Personne.class);
    private GenericDAO<Permission> daoPermission = new GenericDAO<>(Permission.class);
    private GenericDAO<Rolepermission> daoRolepermission = new GenericDAO<>(Rolepermission.class);

    @PostConstruct
    public void init() {

    	List<Personne> employees = daoPersonne.findAll();

    	allPermissions = daoPermission.findAll();

    	for (Personne emp : employees) {
            EmployeePermissionWrapper wrapper = new EmployeePermissionWrapper();
            wrapper.setEmployee(emp);

            Set<Rolepermission> rolePerms = emp.getRolepermissions();
            List<Integer> permIds = new ArrayList<>();
            if (rolePerms != null) {
                for (Rolepermission rp : rolePerms) {
                    permIds.add(rp.getPermission().getIdpermission());
                }
            }
            wrapper.setSelectedPermissionIds(permIds);
            employeePermissions.add(wrapper);
        }
    }


    public void saveEmployeePermissions(EmployeePermissionWrapper wrapper) {
        Personne emp = wrapper.getEmployee();

        List<Rolepermission> existing = daoRolepermission.findRolePermission(emp.getIdpersonne());
        for (Rolepermission rp : existing) {
            daoRolepermission.remove(rp);
        }
        // Créer de nouvelles associations en fonction des permissions sélectionnées
        Set<Rolepermission> newRolePerms = new HashSet<>();
        for (Integer permId : wrapper.getSelectedPermissionIds()) {
            Rolepermission rp = new Rolepermission();

            RolepermissionId rpId = new RolepermissionId(emp.getIdpersonne(), permId);
            rp.setId(rpId);
            Permission perm = daoPermission.findById(permId);
            rp.setPermission(perm);
            rp.setPersonne(emp);
            rp.setDatecreation(LocalDate.now().toString());
            rp.setEtat("Active");
            daoRolepermission.persist(rp);
            newRolePerms.add(rp);
        }

        emp.setRolepermissions(newRolePerms);
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Permissions mises à jour pour " + emp.getPrenom() + " " + emp.getNom()));
    }


    public void saveAll() {
        for (EmployeePermissionWrapper wrapper : employeePermissions) {
            saveEmployeePermissions(wrapper);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Toutes les permissions mises à jour"));
    }

    // Getters et Setters
    public List<EmployeePermissionWrapper> getEmployeePermissions() {
        return employeePermissions;
    }
    public void setEmployeePermissions(List<EmployeePermissionWrapper> employeePermissions) {
        this.employeePermissions = employeePermissions;
    }
    public List<Permission> getAllPermissions() {
        return allPermissions;
    }
    public void setAllPermissions(List<Permission> allPermissions) {
        this.allPermissions = allPermissions;
    }

    // Classe interne pour encapsuler un employé et ses permissions sélectionnées
    public static class EmployeePermissionWrapper implements Serializable {
        private Personne employee;
        private List<Integer> selectedPermissionIds = new ArrayList<>();

        public Personne getEmployee() {
            return employee;
        }
        public void setEmployee(Personne employee) {
            this.employee = employee;
        }
        public List<Integer> getSelectedPermissionIds() {
            return selectedPermissionIds;
        }
        public void setSelectedPermissionIds(List<Integer> selectedPermissionIds) {
            this.selectedPermissionIds = selectedPermissionIds;
        }
    }
}
