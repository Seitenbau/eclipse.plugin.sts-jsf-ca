package jsfca;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsf.context.symbol.IBeanInstanceSymbol;
import org.eclipse.jst.jsf.context.symbol.IJavaTypeDescriptor2;
import org.eclipse.jst.jsf.context.symbol.ISymbol;
import org.eclipse.jst.jsf.context.symbol.SymbolFactory;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.model.IBean;
import org.springframework.ide.eclipse.beans.core.model.IBeansComponent;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfig;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfigSet;
import org.springframework.ide.eclipse.beans.core.model.IBeansProject;

public class SpringBeansSymbolsProcessor {

	private static final String SPRING_INTERNAL_ANNOTATION_PACKAGE = "org.springframework.context.annotation.internal";

	private final IJavaProject javaProject;

	private final IBeansProject springProject;

	private final List<ISymbol> symbols = new Vector<ISymbol>();

	public static SpringBeansSymbolsProcessor create(IJavaProject project){
		return new SpringBeansSymbolsProcessor(project);
	}
	
	private SpringBeansSymbolsProcessor(IJavaProject project) {
		this.javaProject = project;
		this.springProject = getSpringProject(project);
		process();
	}

	public List<ISymbol> getSymbols() {
		return symbols;
	}

	void process() {
		Set<IBeansConfig> configs = new HashSet<IBeansConfig>();
		configs.addAll(springProject.getConfigs());
		for (IBeansConfigSet configSet : springProject.getConfigSets()) {
			Set<IBeansConfig> bcs = configSet.getConfigs();
			configs.addAll(bcs);
		}
		for (IBeansConfig bc : configs) {
			processSpringComponents(bc.getComponents());
		}
	}

	void processSpringComponents(Set<IBeansComponent> components) {
		if (isNotEmpty(components)) {
			for (IBeansComponent component : components) {
				Set<IBean> springBeans = component.getBeans();
				for (IBean springBean : springBeans) {
					if (!isSpringInternalBean(springBean)) {
						IBeanInstanceSymbol springBeanSymbol = createBeanSymbol();
						springBeanSymbol.setName(springBean.getElementName());
						IJavaTypeDescriptor2 javaTypeDescriptor = createJavaTypeDescriptor();
						IType beanType = findType(springBean.getClassName());
						javaTypeDescriptor.setType(beanType);
						springBeanSymbol
								.setJavaTypeDescriptor(javaTypeDescriptor);
						symbols.add(springBeanSymbol);
					}
				}
				processSpringComponents(component.getComponents());
			}
		}
	}

	private IBeansProject getSpringProject(IJavaProject project) {
		return BeansCorePlugin.getModel().getProject(project.getProject());
	}

	private boolean isNotEmpty(Set<IBeansComponent> components) {
		return components != null && !components.isEmpty();
	}

	private IJavaTypeDescriptor2 createJavaTypeDescriptor() {
		return SymbolFactory.eINSTANCE.createIJavaTypeDescriptor2();
	}

	private IBeanInstanceSymbol createBeanSymbol() {
		return SymbolFactory.eINSTANCE.createIBeanInstanceSymbol();
	}

	private IType findType(String className) {
		IType beanType = null;
		try {
			beanType = javaProject.findType(className);
		} catch (JavaModelException e) {
			// do nothing
		}
		return beanType;
	}

	private boolean isSpringInternalBean(IBean bean) {
		return bean.getElementName() == null
				|| bean.getElementName().startsWith(
						SPRING_INTERNAL_ANNOTATION_PACKAGE);
	}

}
