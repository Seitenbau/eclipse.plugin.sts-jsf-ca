package jsfca;

import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import jsfca.preferences.JsfcaPerferenceConstants;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsf.context.symbol.IBeanInstanceSymbol;
import org.eclipse.jst.jsf.context.symbol.IJavaTypeDescriptor2;
import org.eclipse.jst.jsf.context.symbol.ISymbol;
import org.eclipse.jst.jsf.context.symbol.SymbolFactory;

public class RegexSymbolsProcessor {
	
	
	private final IJavaProject javaProject;
	
	private final List<ISymbol> symbols = new Vector<ISymbol>();
	
	public RegexSymbolsProcessor(IJavaProject javaProject) {
		this.javaProject = javaProject;
		process();
	}
	
	private void process() {
		try {
			IPreferenceStore preferenceStore = 
					Activator.getDefault().getPreferenceStore();

			String basepackage = preferenceStore
					.getString(JsfcaPerferenceConstants.PROP_KEY_BASE_PACKAGE);
			String regEx = preferenceStore
					.getString(JsfcaPerferenceConstants.PROP_KEY_FILE_PATTERN);

			if (basepackage == null || basepackage.isEmpty()) {
				return;
			}

			if (regEx == null || regEx.isEmpty()) {
				return;
			}

			Pattern classPattern = Pattern.compile(regEx);

			IPackageFragment[] packageFragments = javaProject.getPackageFragments();
			for (IPackageFragment packageFragment : packageFragments) {
				if (packageFragment.getElementName().startsWith(
						basepackage.toLowerCase())) {
					if (packageFragment.containsJavaResources()) {
						ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
						for (ICompilationUnit compUnit : compilationUnits) {
							IType primaryType = compUnit.findPrimaryType();
							if (primaryType != null
									&& classPattern.matcher(
											primaryType.getElementName())
											.matches()) {
								final IBeanInstanceSymbol bean = SymbolFactory.eINSTANCE
										.createIBeanInstanceSymbol();
								bean.setName(getBeanName(primaryType
										.getElementName()));
								IJavaTypeDescriptor2 beanJavaTypeDescriptor = SymbolFactory.eINSTANCE
										.createIJavaTypeDescriptor2();
								beanJavaTypeDescriptor.setType(primaryType);
								bean.setJavaTypeDescriptor(beanJavaTypeDescriptor);
								symbols.add(bean);
							}
						}
					}
				}
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private String getBeanName(String simpleClassName) {
		StringBuilder sb = new StringBuilder();
		sb.append(simpleClassName.substring(0, 1).toLowerCase());
		sb.append(simpleClassName.substring(1));
		return sb.toString();
	}

	public List<ISymbol> getSymbols() {
		return symbols;
	}

	
}
