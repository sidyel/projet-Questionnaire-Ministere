package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.dao.GenericDAO;
import org.entitee.Itemquestion;
import org.entitee.Question;
import org.primefaces.PrimeFaces;

@Named("itemquestionBean")
@ViewScoped
public class ItemquestionBean implements Serializable {

    private List<Itemquestion> items = new ArrayList<>();
    private Itemquestion selectedItem; 
    private List<Itemquestion> selectedItems = new ArrayList<>(); // Renommé de selectedQuestions en selectedItems
    private Question selectedQuestion;
    private int questionId;

    private GenericDAO<Itemquestion> daoItem = new GenericDAO<>(Itemquestion.class);
    private GenericDAO<Question> daoQuestion = new GenericDAO<>(Question.class);

    @PostConstruct
    public void init() {
        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("id");
        try {
            questionId = Integer.parseInt(idParam);
        } catch (Exception e) {
            questionId = 0;
        }
        if (questionId != 0) {
            selectedQuestion = daoQuestion.findById(questionId);
            items = daoItem.finditemsQuestionsByQuestionnaire(questionId);
        } else {
            items = new ArrayList<>();
        }
        initSelectedItem();
    }

    private void initSelectedItem() {
        selectedItem = new Itemquestion();
        if (selectedQuestion != null) {
            selectedItem.setQuestion(selectedQuestion);
        }
    }

    public Itemquestion getSelectedItem() {
        if (selectedItem == null) {
            initSelectedItem();
        }
        return selectedItem;
    }

    public void setSelectedItem(Itemquestion selectedItem) {
        this.selectedItem = selectedItem;
    }

    // Propriété de sélection multiple (renommée en selectedItems)
    public List<Itemquestion> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<Itemquestion> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public void openNew() {
        initSelectedItem();
    }
    
    public String getDeleteButtonMessage() {
        if (selectedItems != null && !selectedItems.isEmpty()) {
            int size = selectedItems.size();
            return size > 1 ? size + " items sélectionnés" : "1 item sélectionné";
        }
        return "Supprimer";
    }
    
    public void loadItems(Question question) {
        this.selectedQuestion = question;
        if (question != null) {
            this.questionId = question.getIdquestion();
            items = daoItem.finditemsQuestionsByQuestionnaire(questionId);
        } else {
            items = new ArrayList<>();
        }
        initSelectedItem();
    }

    public void saveItem() {
        if (selectedItem == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Aucun item à enregistrer"));
            return;
        }
        if (selectedItem.getIditemquestion() == 0) {
            if (selectedQuestion == null) {

                selectedQuestion = daoQuestion.findById(questionId);
            }
            selectedItem.setQuestion(selectedQuestion);
        	selectedItem.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            daoItem.persist(selectedItem);
        } else {
            daoItem.merge(selectedItem);
        }
        items = daoItem.finditemsQuestionsByQuestionnaire(questionId);
        initSelectedItem();
        PrimeFaces.current().ajax().update("form:messages", "form:dt-items");
    }

    
    
    public void deleteItem() {
        if(selectedItem != null) {
            daoItem.remove(selectedItem);
            items = daoItem.finditemsQuestionsByQuestionnaire(questionId);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Question supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-questions");
            selectedQuestion = null;
        }
    }

    public void deleteSelectedItems() {
        if (selectedItems != null && !selectedItems.isEmpty()) {
            for (Itemquestion item : selectedItems) {
                daoItem.remove(item);
            }
            items = daoItem.finditemsQuestionsByQuestionnaire(questionId);
            selectedItems = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Sélection supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-items");
            initSelectedItem();
        }
    }

    // Getters et Setters restants
    public List<Itemquestion> getItems() {
        return items;
    }
    public void setItems(List<Itemquestion> items) {
        this.items = items;
    }
    public Question getSelectedQuestion() {
        return selectedQuestion;
    }
    public void setSelectedQuestion(Question selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
    }
    public int getQuestionId() {
        return questionId;
    }
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}
