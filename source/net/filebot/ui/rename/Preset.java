package net.filebot.ui.rename;

import java.io.File;
import java.util.Locale;

import net.filebot.Language;
import net.filebot.StandardRenameAction;
import net.filebot.WebServices;
import net.filebot.format.ExpressionFilter;
import net.filebot.format.ExpressionFormat;
import net.filebot.ui.rename.FormatDialog.Mode;
import net.filebot.web.Datasource;
import net.filebot.web.EpisodeListProvider;
import net.filebot.web.MovieIdentificationService;
import net.filebot.web.MusicIdentificationService;
import net.filebot.web.SortOrder;

public class Preset {

	public String name;
	public String path;
	public String includes;
	public String format;
	public String database;
	public String sortOrder;
	public String matchMode;
	public String language;
	public String action;

	public Preset(String name, File path, ExpressionFilter includes, ExpressionFormat format, Datasource database, SortOrder sortOrder, String matchMode, Language language, StandardRenameAction action) {
		this.name = name;
		this.path = path == null ? null : path.getPath();
		this.includes = includes == null ? null : includes.getExpression();
		this.format = format == null ? null : format.getExpression();
		this.database = database == null ? null : database.getName();
		this.sortOrder = sortOrder == null ? null : sortOrder.name();
		this.matchMode = matchMode == null ? null : matchMode;
		this.language = language == null ? null : language.getCode();
		this.action = action == null ? null : action.name();
	}

	public String getName() {
		return name;
	}

	public File getInputFolder() {
		return new File(path);
	}

	public ExpressionFilter getIncludeFilter() {
		try {
			return includes == null || includes.isEmpty() ? null : new ExpressionFilter(includes);
		} catch (Exception e) {
			return null;
		}
	}

	public ExpressionFormat getFormat() {
		try {
			return format == null || format.isEmpty() ? null : new ExpressionFormat(format);
		} catch (Exception e) {
			return null;
		}
	}

	public Datasource getDatabase() {
		return WebServices.getDatasourceByName(database);
	}

	public AutoCompleteMatcher getAutoCompleteMatcher() {
		EpisodeListProvider sdb = WebServices.getEpisodeListProvider(database);
		if (sdb != null) {
			return new EpisodeListMatcher(sdb, sdb != WebServices.AniDB, sdb == WebServices.AniDB);
		}

		MovieIdentificationService mdb = WebServices.getMovieIdentificationService(database);
		if (mdb != null) {
			return new MovieHashMatcher(mdb);
		}

		MusicIdentificationService adb = WebServices.getMusicIdentificationService(database);
		if (adb != null) {
			return new AudioFingerprintMatcher(adb);
		}

		throw new IllegalStateException(database);
	}

	public Mode getMode() {
		EpisodeListProvider sdb = WebServices.getEpisodeListProvider(database);
		if (sdb != null) {
			return Mode.Episode;
		}

		MovieIdentificationService mdb = WebServices.getMovieIdentificationService(database);
		if (mdb != null) {
			return Mode.Movie;
		}

		MusicIdentificationService adb = WebServices.getMusicIdentificationService(database);
		if (adb != null) {
			return Mode.Music;
		}

		return Mode.File;
	}

	public String getMatchMode() {
		return matchMode == null || matchMode.isEmpty() ? null : matchMode;
	}

	public SortOrder getSortOrder() {
		try {
			return SortOrder.forName(sortOrder);
		} catch (Exception e) {
			return null;
		}
	}

	public Locale getLanguage() {
		return language == null || language.isEmpty() ? null : new Locale(language);
	}

	public StandardRenameAction getRenameAction() {
		try {
			return StandardRenameAction.forName(action);
		} catch (Exception e) {
			return null;
		}
	}

}