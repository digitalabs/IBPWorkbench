package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class EditBreedingMethodsWindow extends BaseSubWindow {

	private static final long serialVersionUID = 3983198771242295731L;
	private static final Logger LOG = LoggerFactory.getLogger(EditBreedingMethodsWindow.class);

	private BreedingMethodForm breedingMethodForm;

	private Button cancelButton;

	private Button editBreedingMethodButton;

	private Component buttonArea;

	private VerticalLayout layout;

	private final ProgramMethodsPresenter presenter;

	protected MethodView modelBean;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private BreedingMethodTracker breedingMethodTracker;

	public EditBreedingMethodsWindow(final ProgramMethodsPresenter presenter, final MethodView methodView) {
		this.presenter = presenter;

		this.modelBean = methodView;

		this.assemble();
	}

	protected void initializeComponents() {
		this.breedingMethodForm = new BreedingMethodForm(this.presenter.getMethodClasses(), this.modelBean);
		this.breedingMethodForm.setDebugId("breedingMethodForm");

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
		this.editBreedingMethodButton = new Button("Save");
		this.editBreedingMethodButton.setDebugId("editBreedingMethodButton");
		this.editBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.buttonArea = this.layoutButtonArea();
	}

	protected void initializeLayout() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		this.setWidth("700px");
		this.setResizable(false);
		this.center();
		this.setCaption("Edit Breeding Method");

		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.layout = new VerticalLayout();
		this.layout.setDebugId("layout");
		this.layout.setWidth("100%");
		this.layout.setHeight("450px");

		final Panel p = new Panel();
		p.setDebugId("p");
		p.setStyleName("form-panel");
		p.setSizeFull();

		final VerticalLayout vl = new VerticalLayout();
		vl.setDebugId("vl");
		vl.setSizeFull();
		vl.addComponent(
				new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
		vl.addComponent(this.breedingMethodForm);
		vl.setExpandRatio(this.breedingMethodForm, 1.0F);

		p.addComponent(vl);
		this.layout.addComponent(p);
		this.layout.addComponent(this.buttonArea);

		this.layout.setExpandRatio(p, 1.0F);
		this.layout.setComponentAlignment(this.buttonArea, Alignment.MIDDLE_CENTER);

		this.layout.setSpacing(true);
		this.layout.setMargin(true);

		this.setContent(this.layout);
	}

	protected void initializeActions() {

		this.editBreedingMethodButton.addListener(new EditBreedingMethodButtonListener());

		this.cancelButton.addListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2336400725451747344L;

			@Override
			public void buttonClick(final Button.ClickEvent clickEvent) {

				clickEvent.getComponent().getWindow().getParent().removeWindow(clickEvent.getComponent().getWindow());

			}
		});
	}

	protected Component layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
		this.editBreedingMethodButton = new Button("Save");
		this.editBreedingMethodButton.setDebugId("editBreedingMethodButton");
		this.editBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.editBreedingMethodButton);

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	public void setBreedingMethodForm(final BreedingMethodForm breedingMethodForm) {
		this.breedingMethodForm = breedingMethodForm;
	}

	public void setBreedingMethodTracker(final BreedingMethodTracker breedingMethodTracker) {
		this.breedingMethodTracker = breedingMethodTracker;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	class EditBreedingMethodButtonListener implements Button.ClickListener {


		/**
		 *
		 */
		private static final long serialVersionUID = 5290520698158469871L;
		public static final String BREEDING_METHOD_IS_UPDATED = " breeding method is updated.";
		public static final String PLEASE_SELECT_A_GENERATION_ADVANCEMENT_TYPE = "Please select a Generation Advancement Type";

		@Override
		public void buttonClick(final Button.ClickEvent clickEvent) {

			try {
				EditBreedingMethodsWindow.this.breedingMethodForm.commit();
			} catch (final Validator.EmptyValueException e) {
				MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());
				LOG.warn(e.getMessage(), e);
				return;
			} catch (final Validator.InvalidValueException e) {
				MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());
				LOG.warn(e.getMessage(), e);
				return;
			}

			breedingMethodTracker.getUniqueBreedingMethods().remove(EditBreedingMethodsWindow.this.modelBean);
			breedingMethodTracker.getProjectBreedingMethodData().remove(EditBreedingMethodsWindow.this.modelBean.getMid());

			final MethodView bean =
					((BeanItem<MethodView>) EditBreedingMethodsWindow.this.breedingMethodForm.getItemDataSource()).getBean();
			if (StringUtils.isEmpty(bean.getMtype())) {
				MessageNotifier
						.showRequiredFieldError(clickEvent.getComponent().getWindow(), PLEASE_SELECT_A_GENERATION_ADVANCEMENT_TYPE);
				return;
			}

			final MethodView result = EditBreedingMethodsWindow.this.presenter.editBreedingMethod(bean);

			MessageNotifier.showMessage(clickEvent.getComponent().getWindow().getParent().getWindow(),
					EditBreedingMethodsWindow.this.messageSource.getMessage(Message.SUCCESS),
					result.getMname() + BREEDING_METHOD_IS_UPDATED);

			EditBreedingMethodsWindow.this.getParent().removeWindow(EditBreedingMethodsWindow.this);
		}

	}

}
