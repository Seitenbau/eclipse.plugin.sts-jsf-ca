package jsfca;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsf.context.symbol.ISymbol;
import org.eclipse.jst.jsf.context.symbol.source.AbstractSymbolSourceProviderFactory;
import org.eclipse.jst.jsf.context.symbol.source.ISymbolSourceProvider;
import org.eclipse.jst.jsf.designtime.symbols.FileContextUtil;
import org.eclipse.jst.jsf.designtime.symbols.SymbolUtil;

public class SpringBeanProviderFactory extends
		AbstractSymbolSourceProviderFactory implements ISymbolSourceProvider {

	/**
	 * @see org.eclipse.jst.jsf.context.symbol.source.ISymbolSourceProvider#getSymbols(org.eclipse.core.runtime.IAdaptable,int)
	 */
	@Override
	public ISymbol[] getSymbols(IAdaptable context, int symbolScopeMask) {
		final List<ISymbol> symbols = new ArrayList<ISymbol>();
		IJavaProject javaProject = JavaCore.create(this.getProject());

		List<ISymbol> springSymbols = SpringBeansSymbolsProcessor.create(javaProject).getSymbols();
		symbols.addAll(springSymbols);

		RegexSymbolsProcessor regexSymbolsProcessor = new RegexSymbolsProcessor(javaProject);
		symbols.addAll(regexSymbolsProcessor.getSymbols());

		return symbols.toArray(new ISymbol[symbols.size()]);
	}

	/**
	 * @see org.eclipse.jst.jsf.context.symbol.source.ISymbolSourceProvider#getSymbols(java.lang.String,
	 *      org.eclipse.core.runtime.IAdaptable, int)
	 */
	@Override
	public ISymbol[] getSymbols(String prefix, IAdaptable context,
			int symbolScopeMask) {
		return SymbolUtil.filterSymbolsByPrefix(
				getSymbols(context, symbolScopeMask), prefix);
	}

	/**
	 * @see org.eclipse.jst.jsf.context.symbol.source.ISymbolSourceProvider#isProvider(org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public boolean isProvider(IAdaptable context) {
		IFile file = FileContextUtil.deriveIFileFromContext(context);
		return (file != null && file.getProject() == this.getProject());
	}

	/**
	 * @see org.eclipse.jst.jsf.context.symbol.source.AbstractSymbolSourceProviderFactory#create(org.eclipse.core.resources.IProject)
	 */
	@Override
	protected ISymbolSourceProvider create(IProject project) {
		return this;
	}
}
