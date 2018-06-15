package simlpe.microservices.vaadinweb.ui;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.shared.ScrollBehavior;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import simlpe.microservices.vaadinweb.model.Poem;
import simlpe.microservices.vaadinweb.model.Tag;
import simlpe.microservices.vaadinweb.service.PoemService;
import simlpe.microservices.vaadinweb.service.TagService;
import simlpe.microservices.vaadinweb.service.TokenService;
import simlpe.microservices.vaadinweb.service.UserActivityService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringUI
@PreserveOnRefresh
public class VaadinUI extends UI {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private PoemService poemService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserActivityService userActivityService;

    private AutocompleteTextField field;
    private TextArea poemArea;
    private HorizontalLayout tagRow;

    private String token;
    private String username;
    private Set<String> chosenTags;
    private List<Button> tagButtons;

    @Override
    protected void init(VaadinRequest request) {
        setContent(getLoginContent());
    }

    private Component getLoginContent() {

        LoginForm loginForm = new LoginForm();
        Layout verticalLayout = new VerticalLayout();

        loginForm.addLoginListener((e) -> {

            try {
                username = e.getLoginParameter("username");

                token = tokenService.getToken(username,
                        e.getLoginParameter("password"));
                System.out.println(token);
            } catch (Exception e1) {
                e1.getMessage();
            }

            if (token != null) {
                setContent(getPoemContent());
                userActivityService.notifyActivity(username, token);
            } else
                Notification.show("Wrong username or password");
        });

        verticalLayout.addComponent(loginForm);
        verticalLayout.setSizeFull();
        ((VerticalLayout) verticalLayout).setComponentAlignment(loginForm, Alignment.TOP_CENTER);

        return verticalLayout;
    }

    private Component getPoemContent() {

        // get set of tags
        Set<Tag> tags = tagService.getTags();
        Set<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toSet());

        //init set of chosen tags
        chosenTags = new HashSet<>();

        //main layout
        Layout verticalLayout = new VerticalLayout();
        ((VerticalLayout) verticalLayout).setDefaultComponentAlignment(Alignment.TOP_LEFT);

        //panel for tags and input
        Panel panel = new Panel();
        panel.setWidth("100%");

        Label label = new Label("Tags: ");

        Button addTagButton = new Button("Add tag");
        Button savePoemButton = new Button("Save poem");

        tagButtons = new ArrayList<>();

        //config components
        tagRow = new HorizontalLayout();
        tagRow.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        tagRow.setSizeFull();

        tagRow.addComponent(label);

        poemArea = new TextArea("Enter your poem");
        poemArea.setWidth("100%");
        poemArea.setHeight("100%");

        field = new AutocompleteTextField();
        field.setSuggestionProvider(new CollectionSuggestionProvider(tagNames));
        field.setDelay(10); // Delay before sending a query to the server
        field.setItemAsHtml(false); // Suggestions contain html formating. If true, make sure that the html is save to use!
        field.setMinChars(1); // The required value length to trigger a query
        field.setScrollBehavior(ScrollBehavior.NONE); // The method that should be used to compensate scrolling of the page
        field.setSuggestionLimit(0); // The max amount of suggestions send to the client. If the limit is >= 0 no limit is applied
        field.addSelectListener(selectEvent -> addTag());
        field.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        field.setWidth("100%");
        field.setHeight("30%");

        addTagButton.addClickListener(clickEvent -> addTag());
        addTagButton.setClickShortcut(ShortcutAction.KeyCode.ENTER, ShortcutAction.ModifierKey.CTRL);
        addTagButton.setVisible(true);

        savePoemButton.addClickListener(clickEvent -> savePoem());

        tagRow.addComponent(field);
        tagRow.setExpandRatio(field, 1.0f);
        panel.setContent(tagRow);

        verticalLayout.addComponent(poemArea);
        verticalLayout.addComponent(panel);
        verticalLayout.addComponent(addTagButton);
        verticalLayout.addComponent(savePoemButton);

        return verticalLayout;
    }

    private void addTag() {

        String tagName = field.getValue();
        tagName = StringUtils.deleteWhitespace(tagName);
        field.clear();
        field.detach();
        drawTag(tagName);
        chosenTags.add(tagName);
        tagRow.addComponent(field);
        tagRow.setExpandRatio(field, 1.0f);
    }

    private void drawTag(String tagName) {
        if (!StringUtils.isBlank(tagName) && !chosenTags.contains(tagName)) {
            Button tag = new Button(tagName);
            tag.addStyleName("primary");
            tag.addStyleName(ValoTheme.BUTTON_TINY);
            tag.setSizeUndefined();

            tagButtons.add(tag);

            tag.addClickListener(clickEvent -> {
                chosenTags.remove(tag.getCaption());
                tagRow.removeComponent(tag);
            });
            tagRow.addComponent(tag);
        }
    }

    private void savePoem() {

        Set<Tag> tags = chosenTags.stream()
                .map(Tag::new)
                .collect(Collectors.toSet());

        Poem poem = new Poem(username,
                poemArea.getValue(),
                tags);
        poemService.savePoem(poem, token);
        poemArea.clear();

        for (Button button: tagButtons) {
            button.click();
        }
        tagButtons.clear();

        userActivityService.notifyActivity(username, token);
    }

    @Override
    public void doRefresh(VaadinRequest request) {

        if(token != null && username != null)
            userActivityService.notifyActivity(username, token);
    }
}
