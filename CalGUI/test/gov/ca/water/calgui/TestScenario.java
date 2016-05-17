package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.FileUtils;
import gov.ca.water.calgui.utils.GUIUtils;

import java.io.File;

import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestScenario {

	private MainMenu mm = null;

	@Before
	public void setUp() {

		// Give Cal-Lite a chance to initialize fully.

		System.out.println("Initializing UI.");

		try {
			mm = new MainMenu(false);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Give Cal-Lite a chance to initialize fully.
		try {

			System.out.println("Pausing for a few seconds for initialization to complete ...");

			Thread.sleep(4000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@After
	public void cleanUp() {
		// clean up scenario files and directories here
	}

	@Test
	/**
	 * Test that we can write files for Default scenario and match test 
	 */
	public void testDefault() {

		// 1. Erase scenario record directory for default

		File target = new File(System.getProperty("user.dir") + "\\Scenarios\\Run_Details\\default");
		FileUtils.deleteDir(target);

		// 2. load default scenari0

		File file = new File(System.getProperty("user.dir") + "\\Scenarios\\default.cls");

		mm.regUserEditFlags = GUIUtils.setControlValues(file, MainMenu.getSwix(), mm.dTableModels, mm.gl, mm.regUserEditFlags);
		mm.regFlags = GUIUtils.setControlValues(file, MainMenu.getSwix(), mm.gl, mm.regFlags);

		// 3. Generate

		RunUtils.setupScenario("default.cls", "", MainMenu.desktop, MainMenu.getSwix(), mm.regUserEditFlags, mm.dTableModels,
		        mm.gl, mm.regFlags);

		// 4. Wait for save to complete ..

		try {

			for (int i = 0; i < 30 && !ScenarioMonitor.getStatus("default").contains("SAVED"); i++)
				Thread.sleep(1000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// now check generated files vs baseline generated files for this scenario

		File directory1 = new File("test-resources/default_scenario/Generated/Lookup");
		File directory2 = new File(System.getProperty("user.dir") + "\\Scenarios\\Run_Details\\default\\Generated\\Lookup");
		FileAssert.assertDirectoryAllFilesWithSameNameHaveSameContent("Default scenario comparison", directory1, directory2);

	}

	@Test
	/**
	 * Test that we can create a D1641 scenario programatically and generate files that match the test resource.
	 */
	public void testD1641() {

		// 1. Erase scenario record directory

		File target = new File(System.getProperty("user.dir") + "\\Scenarios\\Run_Details\\D1641");
		FileUtils.deleteDir(target);

		// 2. Load default scenario

		File file = new File(System.getProperty("user.dir") + "\\Scenarios\\default.cls");
		mm.regUserEditFlags = GUIUtils.setControlValues(file, MainMenu.getSwix(), mm.dTableModels, mm.gl, mm.regUserEditFlags);
		mm.regFlags = GUIUtils.setControlValues(file, MainMenu.getSwix(), mm.gl, mm.regFlags);

		// 3. Set to D1641 by simulating quick-select of rdbRegQS_D1641

		((JRadioButton) (MainMenu.getSwix().find("rdbRegQS_D1641"))).setSelected(true);

		// 4. Save

		((JTextField) MainMenu.getSwix().find("run_txfScen")).setText("D1641.cls");
		RunUtils.setFilenameTooltips();
		StringBuffer sb = RunUtils
		        .buildScenarioString(MainMenu.getSwix(), mm.regUserEditFlags, mm.dTableModels, mm.gl, mm.regFlags);
		RunUtils.saveScenarioFile(sb, System.getProperty("user.dir") + "\\Scenarios\\D1641.cls");

		// 5. Generate

		RunUtils.setupScenario("D1641.cls", "", MainMenu.desktop, MainMenu.getSwix(), mm.regUserEditFlags, mm.dTableModels, mm.gl,
		        mm.regFlags);

		// 6. Wait for save to complete ..

		try {
			for (int i = 0; i < 30 && !ScenarioMonitor.getStatus("D1641").contains("SAVED"); i++)
				Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 7. Check generated files vs baselined generated files for this scenario

		File directory1 = new File("test-resources/D1641/Generated/Lookup");
		File directory2 = new File(System.getProperty("user.dir") + "\\Scenarios\\Run_Details\\default\\Generated\\D1641");
		FileAssert.assertDirectoryAllFilesWithSameNameHaveSameContent("Default scenario comparison", directory1, directory2);

	}
}
