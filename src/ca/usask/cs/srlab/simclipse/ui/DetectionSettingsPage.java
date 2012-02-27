package ca.usask.cs.srlab.simclipse.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ca.usask.cs.srlab.simcad.Constants;
import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

public class DetectionSettingsPage extends DialogPage{

	private boolean fIsCaseSensitive;
	private boolean fIsRegExSearch;
	private boolean fSearchDerived;

	private Combo fPattern;
	private Button fIsCaseSensitiveCheckbox;
	private Combo fExtensions;
	private Button fIsRegExCheckbox;
	private CLabel fStatusLabel;
	private Button fSearchDerivedCheckbox;
	
	public DetectionSettingsPage(DetectionSettings existingSettings) {
		super();
	    selectedLanguage = existingSettings.getLanguage();
		selectedGranularity = existingSettings.getCloneGranularity();
		selectedCloneSet = existingSettings.getCloneSetType();
		selectedSourceTransformationApproach = existingSettings.getSourceTransformation();
		selectedCloneTypes.addAll(Arrays.asList(existingSettings.getCloneTypes()));
	}

	private Button add, delete, clear;

	private  String selectedLanguage;
	private  Map<String, Button> languageButtons = new HashMap<String, Button>();

	private  String selectedGranularity;
	private  Map<String, Button> cloneGranulariyButtons = new HashMap<String, Button>();

	private  String selectedCloneSet;
	private  Map<String, Button> cloneSetButtons = new HashMap<String, Button>();

	private  String selectedSourceTransformationApproach;
	private  Map<String, Button> sourceTransformationApproachButtons = new HashMap<String, Button>();

	private  Set<String> selectedCloneTypes = new HashSet<String>();
	private  Map<String, Button> cloneTypeFields = new HashMap<String, Button>();
	

	public boolean performAction(IProject project) {
		DetectionSettings detectionSettings = new DetectionSettings(selectedLanguage, selectedGranularity, selectedCloneSet, selectedSourceTransformationApproach, false, selectedCloneTypes.toArray(new String[0]));
		DetectionSettingsManager.getManager().saveDetectionSettingsForProject(project, detectionSettings);
		return true;
	}

	/*
	 * Implements method from IDialogPage
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	//---- Widget creation ------------------------------------------------

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite result= new Composite(parent, SWT.NONE);
		result.setFont(parent.getFont());
		GridLayout layout= new GridLayout(2, false);
		result.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL); //GridData.FILL_HORIZONTAL //makes the board compact
		data.horizontalSpan = 1;
		result.setLayoutData(data);	
		
//		Group settingsGroup = new Group(parent, SWT.HORIZONTAL);
//		GridLayout settingsGroupLayout = new GridLayout();
//		settingsGroupLayout.numColumns = 1;
//		settingsGroup.setLayout(settingsGroupLayout);
//		GridData data = new GridData(GridData.FILL_HORIZONTAL); //GridData.FILL_HORIZONTAL //makes the board compact
//		data.horizontalSpan = 1;
//		settingsGroup.setLayoutData(data);

		createDetectionSettingsControl(result);
		//createRuntimeSettingControl(result);
		
		setControl(result);
		Dialog.applyDialogFont(result);
}

	
	void createDetectionSettingsControl(Composite settingsGroup) {
		Group detectionSettingsGroup = new Group(settingsGroup, SWT.NONE);
		detectionSettingsGroup.setText("Detection Settings");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		detectionSettingsGroup.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL); //GridData.FILL_HORIZONTAL //makes the board compact
		data.horizontalSpan = 1;
		detectionSettingsGroup.setLayoutData(data);
		createDetectionSettingsWidgets(detectionSettingsGroup);
	}

  void createDetectionSettingsWidgets(Composite detectionSettingsGroup) {
    /* Controls the type of RowLayout */
	  /*
	    final Composite typeCheckboxComposite = new Composite(container, SWT.NONE);
		  final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, false, false, 3, 1);
		  gridData_1.horizontalIndent = 20; 
		  typeCheckboxComposite.setLayoutData(gridData_1);
		  final GridLayout cloneTypesCheckboxLayout = new GridLayout(); 
		  cloneTypesCheckboxLayout.numColumns = 3; 
		  typeCheckboxComposite.setLayout(cloneTypesCheckboxLayout);
		  */
	     
	//language
		
	    Group languageGroup = new Group(detectionSettingsGroup, SWT.NONE);
	    languageGroup.setText("Source Language");
	    GridLayout languageGroupCheckboxLayout = new GridLayout();
	    //languageGroupCheckboxLayout.numColumns = 4;
	    languageGroup.setLayout(languageGroupCheckboxLayout);
	    //GridData data_l = new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1);
	    GridData data_l = new GridData(GridData.FILL_BOTH);
	    //data_l.horizontalIndent = 30;
	    languageGroup.setLayoutData(data_l);
		
		String[] allLanguage = new String[]{Constants.LANGUAGE_JAVA, Constants.LANGUAGE_C, Constants.LANGUAGE_CS, Constants.LANGUAGE_PYTHON}; 
		
		for (int i = 0; i < allLanguage.length; i++) {
		   final String eachType = allLanguage[i]; 
		   final Button button = new Button(languageGroup, SWT.RADIO); 
		   button.setText(eachType); 
		   
		   button.setSelection(eachType.equals(selectedLanguage));
		   
		   languageButtons.put(eachType, button); 
		   
		   button.addSelectionListener(new SelectionAdapter() {
		   
				public void widgetSelected(SelectionEvent e) {
					if (button.getSelection()) {
						selectedLanguage = eachType;
					}
				}
		   });
		} 
	  
	  
	  
	  //clone types  
    Group cloneTypeGroup = new Group(detectionSettingsGroup, SWT.NONE);
    cloneTypeGroup.setText("Clone Type");
    cloneTypeGroup.setLayout(new GridLayout());
    ((GridLayout)cloneTypeGroup.getLayout()).marginHeight = 20;
    GridData data = new GridData(GridData.FILL_BOTH);
    cloneTypeGroup.setLayoutData(data);
   
		String[] allCloneTypes = new String[]{CloneSet.CLONE_TYPE_1, CloneSet.CLONE_TYPE_2, CloneSet.CLONE_TYPE_3}; 
		
		for (int i = 0; i < allCloneTypes.length; i++) {
		   final String eachType = allCloneTypes[i]; 
		   if (eachType == null)
		   continue;
		   
		   final Button button = new Button(cloneTypeGroup, SWT.CHECK); 
		   button.setText(eachType); 
		   
		   button.setSelection(selectedCloneTypes!=null && selectedCloneTypes.contains(eachType));
		   
		   cloneTypeFields.put(eachType, button); 
		   
		   button.addSelectionListener(new SelectionAdapter() {
		   
				public void widgetSelected(SelectionEvent e) {
					if (button.getSelection()) {
						selectedCloneTypes.add(eachType);
					} else {
						if(!isValidAction())
							button.setSelection(true);
						else
							selectedCloneTypes.remove(eachType);
					}
				}
			   
			   private boolean isValidAction(){
				   for(Button button : cloneTypeFields.values()){
					   if(button.getSelection())
						   return true;
				   }
				   return false;
			   }			   
		   
		   });
		} 
		
		
		//clone granularity
		
	    Group cloneGranularityGroup = new Group(detectionSettingsGroup, SWT.NONE);
	    cloneGranularityGroup.setText("Clone Granularity");
	    cloneGranularityGroup.setLayout(new GridLayout());
	    GridData data_g = new GridData(GridData.FILL_BOTH);
	    cloneGranularityGroup.setLayoutData(data_g);
		
		String[] allCloneGranularities = new String[]{Constants.CLONE_GRANULARITY_FUNTION, Constants.CLONE_GRANULARITY_BLOCK}; 
		
		for (int i = 0; i < allCloneGranularities.length; i++) {
		   final String eachType = allCloneGranularities[i]; 
		   final Button button = new Button(cloneGranularityGroup, SWT.RADIO); 
		   button.setText(eachType); 
		   
		   button.setSelection(eachType.equals(selectedGranularity));
		   
		   cloneGranulariyButtons.put(eachType, button); 
		   
		   button.addSelectionListener(new SelectionAdapter() {
		   
				public void widgetSelected(SelectionEvent e) {
					if (button.getSelection()) {
						selectedGranularity = eachType;
					}
				}
		   });
		} 
		
		//clone set
	    Group cloneSetGroup = new Group(detectionSettingsGroup, SWT.NONE);
	    cloneSetGroup.setText("Clone Grouping");
	    cloneSetGroup.setLayout(new GridLayout());
	    GridData data_c = new GridData(GridData.FILL_BOTH);
	    cloneSetGroup.setLayoutData(data_c);
		
		String[] allCloneSets = new String[]{Constants.CLONE_SET_TYPE_GROUP, Constants.CLONE_SET_TYPE_PAIR}; 
		
		for (int i = 0; i < allCloneSets.length; i++) {
		   final String eachType = allCloneSets[i]; 
		   final Button button = new Button(cloneSetGroup, SWT.RADIO); 
		   button.setText(eachType); 
		  
		   button.setSelection(eachType.equals(selectedCloneSet));
		   
		   cloneSetButtons.put(eachType, button); 
		   
		   button.addSelectionListener(new SelectionAdapter() {
		   
				public void widgetSelected(SelectionEvent e) {
					if (button.getSelection()) {
						selectedCloneSet = eachType;
					}
				}
		   });
		} 
    
		//source transformations
	    Group sourceTransforrmGroup = new Group(detectionSettingsGroup, SWT.NONE);
	    sourceTransforrmGroup.setText("Source Transformation");
	    sourceTransforrmGroup.setLayout(new GridLayout());
	    GridData data_s = new GridData(GridData.FILL_BOTH);
	    sourceTransforrmGroup.setLayoutData(data_s);
		
		String[] allSourceTransformations = new String[]{Constants.SOURCE_TRANSFORMATION_APPROACH_GENEROUS, Constants.SOURCE_TRANSFORMATION_APPROACH_GREEDY}; 
		
		for (int i = 0; i < allSourceTransformations.length; i++) {
		   final String eachType = allSourceTransformations[i]; 
		   final Button button = new Button(sourceTransforrmGroup, SWT.RADIO); 
		   button.setText(eachType); 
		   
		   button.setSelection(eachType.equals(selectedSourceTransformationApproach));
		   
		   sourceTransformationApproachButtons.put(eachType, button); 
		   
		   button.addSelectionListener(new SelectionAdapter() {
		   
				public void widgetSelected(SelectionEvent e) {
					if (button.getSelection()) {
						selectedSourceTransformationApproach = eachType;
					}
				}
		   });
		} 
		
  }
  
  /**
   * Creates the "child" group. This is the group that allows you to add
   * children to the layout. It exists within the controlGroup.
   */
  void createRuntimeSettingControl(Composite settingsGroup) {
	Group runtimeSettingsGroup = new Group(settingsGroup, SWT.NONE);
    runtimeSettingsGroup.setText("Runtime Settings");
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    runtimeSettingsGroup.setLayout(layout);
    GridData data = new GridData(GridData.FILL_BOTH);
    data.horizontalSpan = 2;
    runtimeSettingsGroup.setLayoutData(data);
    createRuntimeSettingWidgets(runtimeSettingsGroup);
  }

  
  /**
   * Creates the controls for modifying the "children" table, and the table
   * itself. Subclasses override this method to augment the standard table.
   */
  void createRuntimeSettingWidgets(Composite runtimeSettingsGroup) {
    /* Controls for adding and removing children */
    add = new Button(runtimeSettingsGroup, SWT.PUSH);
    add.setText("Add");
    add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    delete = new Button(runtimeSettingsGroup, SWT.PUSH);
    delete.setText("Delete");
    delete.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    delete.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
      }
    });
    clear = new Button(runtimeSettingsGroup, SWT.PUSH);
    clear.setText("Clear");
    clear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    clear.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
      }
    });

  }
	
	
	
	private void addTextPatternControls(Composite group) {
		// grid layout with 2 columns

		// Info text
		Label label= new Label(group, SWT.LEAD);
		label.setText("SearchMessages.SearchPage_containingText_text");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label.setFont(group.getFont());

		// Pattern combo
		fPattern= new Combo(group, SWT.SINGLE | SWT.BORDER);
		// Not done here to prevent page from resizing
		// fPattern.setItems(getPreviousSearchPatterns());
		fPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
		// add some listeners for regex syntax checking
		fPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			}
		});
		fPattern.setFont(group.getFont());
		GridData data= new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint= convertWidthInCharsToPixels(50);
		fPattern.setLayoutData(data);

		fIsCaseSensitiveCheckbox= new Button(group, SWT.CHECK);
		fIsCaseSensitiveCheckbox.setText("SearchMessages.SearchPage_caseSensitive");
		fIsCaseSensitiveCheckbox.setSelection(fIsCaseSensitive);
		fIsCaseSensitiveCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fIsCaseSensitive= fIsCaseSensitiveCheckbox.getSelection();
			}
		});
		fIsCaseSensitiveCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		fIsCaseSensitiveCheckbox.setFont(group.getFont());

		// Text line which explains the special characters
		fStatusLabel= new CLabel(group, SWT.LEAD);
		fStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fStatusLabel.setFont(group.getFont());
		fStatusLabel.setAlignment(SWT.LEFT);
		fStatusLabel.setText("SearchMessages.SearchPage_containingText_hint");

		// RegEx checkbox
		fIsRegExCheckbox= new Button(group, SWT.CHECK);
		fIsRegExCheckbox.setText("SearchMessages.SearchPage_regularExpression");
		fIsRegExCheckbox.setSelection(fIsRegExSearch);

		fIsRegExCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fIsRegExSearch= fIsRegExCheckbox.getSelection();
			}
		});
		fIsRegExCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		fIsRegExCheckbox.setFont(group.getFont());
	}


	private void addFileNameControls(Composite group) {
		// grid layout with 2 columns

		// Line with label, combo and button
		Label label= new Label(group, SWT.LEAD);
		label.setText("SearchMessages.SearchPage_fileNamePatterns_text");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label.setFont(group.getFont());

		fExtensions= new Combo(group, SWT.SINGLE | SWT.BORDER);
		fExtensions.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			}
		});
		GridData data= new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint= convertWidthInCharsToPixels(50);
		fExtensions.setLayoutData(data);
		fExtensions.setFont(group.getFont());

		Button button= new Button(group, SWT.PUSH);
		button.setText("SearchMessages.SearchPage_browse");
		GridData gridData= new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint= SWTUtil.getButtonWidthHint(button);
		button.setLayoutData(gridData);
		button.setFont(group.getFont());


		// Text line which explains the special characters
		Label description= new Label(group, SWT.LEAD);
		description.setText("SearchMessages.SearchPage_fileNamePatterns_hint");
		description.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		description.setFont(group.getFont());

		fSearchDerivedCheckbox= new Button(group, SWT.CHECK);
		fSearchDerivedCheckbox.setText("SearchMessages.TextSearchPage_searchDerived_label");

		fSearchDerivedCheckbox.setSelection(fSearchDerived);
		fSearchDerivedCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fSearchDerived= fSearchDerivedCheckbox.getSelection();
			}
		});
		fSearchDerivedCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		fSearchDerivedCheckbox.setFont(group.getFont());
  	}

	//--------------- Configuration handling --------------

    /* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

}
