/*******************************************************************************
 * Copyright (c) 2016-2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/

package org.springframework.ide.vscode.commons.languageserver.completion;

import java.util.Comparator;

import org.springframework.ide.vscode.commons.util.Assert;

public abstract class ScoreableProposal implements ICompletionProposal {

	public static final double DEEMP_EXISTS = 0.1;
	public static final double DEEMP_DEPRECATION = 0.2;
	public static final double DEEMP_NEXT_CONTEXT = 0.0;
	public static final double DEEMP_INDENTED_PROPOSAL = 0.4;
	public static final double DEEMP_DASH_PROPOSAL = 0.6;
	public static final double DEEMP_DEDENTED_PROPOSAL = 0.8;

	private static final double DEEMP_VALUE = 10_000; // should be large enough to move deemphasized stuff to bottom of list.

	private double deemphasizedBy = 0.0;

	/**
	 * A sorter suitable for sorting ScoreableProposals based on their score.
	 */
	public static final Comparator<ICompletionProposal> COMPARATOR = new Comparator<ICompletionProposal>() {
		@Override
		public int compare(ICompletionProposal p1, ICompletionProposal p2) {
			if (p1 instanceof ScoreableProposal && p2 instanceof ScoreableProposal) {
				double s1 = ((ScoreableProposal)p1).getScore();
				double s2 = ((ScoreableProposal)p2).getScore();
				if (s1==s2) {
					String name1 = ((ScoreableProposal)p1).getLabel();
					String name2 = ((ScoreableProposal)p2).getLabel();
					return name1.compareTo(name2);
				} else {
					return Double.compare(s2, s1);
				}
			}
			if (p1 instanceof ScoreableProposal) {
				return -1;
			}
			if (p2 instanceof ScoreableProposal) {
				return +1;
			}
			return p1.getLabel().compareTo(p2.getLabel());
		}
	};
	public abstract double getBaseScore();
	public final double getScore() {
		return getBaseScore() - deemphasizedBy;
	}
	@Override
	public ScoreableProposal deemphasize(double howmuch) {
		Assert.isLegal(howmuch>=0.0);
		deemphasizedBy+= howmuch*DEEMP_VALUE;
		return this;
	}
	public boolean isDeemphasized() {
		return deemphasizedBy > 0;
	}

//		@Override
//		public boolean isAutoInsertable() {
//			return !isDeemphasized();
//		}

//		public StyledString getStyledDisplayString() {
//			StyledString result = new StyledString();
//			highlightPattern(getHighlightPattern(), getBaseDisplayString(), result);
//			return result;
//		}

//		private void highlightPattern(String pattern, String data, StyledString result) {
//			Styler highlightStyle = CompletionFactory.HIGHLIGHT;
//			Styler plainStyle = isDeemphasized()?CompletionFactory.DEEMPHASIZE:CompletionFactory.NULL_STYLER;
//			if (isDeprecated()) {
//				highlightStyle = CompletionFactory.compose(highlightStyle, CompletionFactory.DEPRECATE);
//				plainStyle = CompletionFactory.compose(plainStyle, CompletionFactory.DEPRECATE);
//			}
//			if (StringUtils.hasText(pattern)) {
//				int dataPos = 0;	int dataLen = data.length();
//				int patternPos = 0; int patternLen = pattern.length();
//
//				while (dataPos<dataLen && patternPos<patternLen) {
//					int pChar = pattern.charAt(patternPos++);
//					int highlightPos = data.indexOf(pChar, dataPos);
//					if (dataPos<highlightPos) {
//						result.append(data.substring(dataPos, highlightPos), plainStyle);
//					}
//					result.append(data.charAt(highlightPos), highlightStyle);
//					dataPos = highlightPos+1;
//				}
//				if (dataPos<dataLen) {
//					result.append(data.substring(dataPos), plainStyle);
//				}
//			} else { //no pattern to highlight
//				result.append(data, plainStyle);
//			}
//		}

//		protected abstract boolean isDeprecated();
//		protected abstract String getHighlightPattern();
//		protected abstract String getBaseDisplayString();

//		@Override
//		public String getAdditionalProposalInfo() {
//			HoverInfo hoverInfo = getAdditionalProposalInfo(new NullProgressMonitor());
//			if (hoverInfo!=null) {
//				return hoverInfo.getHtml();
//			}
//			return null;
//		}
//		@Override
//		public abstract HoverInfo getAdditionalProposalInfo(IProgressMonitor monitor);

//		@Override
//		public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
//			return null;
//		}
//
//		@Override
//		public int getPrefixCompletionStart(IDocument document, int completionOffset) {
//			return completionOffset;
//		}

	@Override
	public String toString() {
		return getLabel();
	}

}