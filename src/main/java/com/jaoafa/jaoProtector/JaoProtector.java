package com.jaoafa.jaoProtector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.jaoProtector.Event.AntiBlockUnderDestroy;
import com.jaoafa.jaoProtector.Event.Event_AntiItemFrameBreak;
import com.jaoafa.jaoProtector.Event.Event_AntiPeriodBreak;
import com.jaoafa.jaoProtector.Event.Event_Dispenser;
import com.jaoafa.jaoProtector.Event.Event_EndCrystal;
import com.jaoafa.jaoProtector.Event.Event_Fire;
import com.jaoafa.jaoProtector.Event.Event_FireBall;
import com.jaoafa.jaoProtector.Event.Event_Flint_and_steel;
import com.jaoafa.jaoProtector.Event.Event_Hopper;
import com.jaoafa.jaoProtector.Event.Event_ICE;
import com.jaoafa.jaoProtector.Event.Event_Lava;
import com.jaoafa.jaoProtector.Event.Event_MobSpawner;
import com.jaoafa.jaoProtector.Event.Event_SpawnEggRegulation;
import com.jaoafa.jaoProtector.Event.Event_TNT;
import com.jaoafa.jaoProtector.Event.Event_Water;
import com.jaoafa.jaoProtector.Lib.Discord;
import com.jaoafa.jaoProtector.Lib.MySQL;
import com.jaoafa.jaoProtector.Lib.PermissionsManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class JaoProtector extends JavaPlugin {

	public static String sqlserver = "jaoafa.com";
	public static String sqluser;
	public static String sqlpassword;
	public static Connection c = null;
	public static FileConfiguration conf;
	public static JavaPlugin JavaPlugin;
	public static long ConnectionCreate = 0;

	/**
	 * プラグインが起動したときに呼び出し
	 * @author mine_book000
	 * @since 2018/04/30
	 */
	@Override
	public void onEnable() {
		JavaPlugin = this;

		Load_Config(); // Config Load
		if(!this.isEnabled()) return;

		// リスナーを設定
		Import_Listener();
		Import_Task();
		//addFlags();
	}

	/**
	 * リスナー設定
	 * @author mine_book000
	 */
	private void Import_Listener(){
		// 日付は制作完了(登録)の日付
		registEvent(new Event_TNT(this)); // 2018/06/30
		registEvent(new Event_Water(this)); // 2018/06/30
		registEvent(new Event_Lava(this)); // 2018/06/30
		registEvent(new Event_EndCrystal(this)); // 2018/07/08
		registEvent(new Event_Fire(this)); // 2018/07/08
		registEvent(new Event_FireBall(this)); // 2018/07/08
		registEvent(new Event_Flint_and_steel(this)); // 2018/07/08
		registEvent(new Event_MobSpawner(this)); // 2018/07/08
		registEvent(new Event_AntiItemFrameBreak(this)); // 2018/07/17
		registEvent(new Event_AntiPeriodBreak()); // 2018/07/29
		registEvent(new AntiBlockUnderDestroy(this)); // 2018/11/01
		registEvent(new Event_ICE(this)); // 2018/11/10
		registEvent(new Event_Dispenser(this)); // 2018/11/14
		registEvent(new Event_Hopper(this)); // 2018/12/18
		registEvent(new Event_SpawnEggRegulation(this)); // 2018/12/18
	}

	/**
	 * リスナー設定の簡略化用
	 * @param listener Listener
	 */
	private void registEvent(Listener l) {
		getServer().getPluginManager().registerEvents(l, this);
	}

	/**
	 * スケジューリング
	 * @author mine_book000
	 */
	private void Import_Task(){
		new Event_AntiPeriodBreak().runTaskTimerAsynchronously(this, 0L, 200L); // 2018/07/29
	}
	/*
	public static final StateFlag USE_NOTE_BLOCK = new StateFlag("use-note-block", false);

	private void addFlags(){
		WorldGuardPlugin worldGuardPlugin = getWorldGuard();
		FlagRegistry registry = worldGuardPlugin.getFlagRegistry();

		getLogger().info("---------- Add Custom Flag ----------");

		addFlag(registry, USE_NOTE_BLOCK);

		getLogger().info("---------- Add Custom Flag END ----------");
	}

	void addFlag(FlagRegistry registry, StateFlag Flag){
		try {
			registry.register(Flag);
			getLogger().info("Added " + Flag.getName());
		} catch (FlagConflictException e) {
			getLogger().info("Missed " + Flag.getName());
		}
	}
	*/

	@Nullable
	public static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}

	/**
	 * プラグインが停止したときに呼び出し
	 * @author mine_book000
	 * @since 2018/04/30
	 */
	@Override
	public void onDisable() {

	}

	/**
	 * コンフィグ読み込み
	 * @author mine_book000
	 */
	private void Load_Config(){
		conf = getConfig();

		if(conf.contains("discordtoken")){
			Discord.start(this, conf.getString("discordtoken"));
		}else{
			getLogger().info("Discordへの接続に失敗しました。 [conf NotFound]");
			getLogger().info("Disable jaoProtector...");
			getServer().getPluginManager().disablePlugin(this);
		}
		if(conf.contains("sqluser") && conf.contains("sqlpassword")){
			sqluser = conf.getString("sqluser");
			sqlpassword = conf.getString("sqlpassword");
		}else{
			getLogger().info("MySQL Connect err. [conf NotFound]");
			getLogger().info("Disable jaoProtector...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if(conf.contains("sqlserver")){
			sqlserver = (String) conf.get("sqlserver");
		}

		MySQL MySQL = new MySQL(sqlserver, "3306", "jaoafa", sqluser, sqlpassword);

		try {
			c = MySQL.openConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			getLogger().info("MySQL Connect err. [ClassNotFoundException]");
			getLogger().info("Disable jaoProtector...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			getLogger().info("MySQL Connect err. [SQLException: " + e.getSQLState() + "]");
			getLogger().info("Disable jaoProtector...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().info("MySQL Connect successful.");
	}

	public boolean AntiBORDERSave(String name, int i){
		File file = new File(JavaPlugin().getDataFolder(), "AntiBorder.yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		data.set(name, i);
		try {
			data.save(file);
			return true;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
	}
	public static Integer AntiBORDERLoad(String name){
		File file = new File(JavaPlugin().getDataFolder(), "AntiBorder.yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		if(data.contains(name)){
			int i = data.getInt(name);
			return i;
		}else{
			return null;
		}
	}

	/**
	 * CommandSenderに対してメッセージを送信します。
	 * @param sender CommandSender
	 * @param cmd Commandデータ
	 * @param message メッセージ
	 */
	public static void SendMessage(CommandSender sender, Command cmd, String message) {
		sender.sendMessage("[" + cmd.getName().toUpperCase() +"] " + ChatColor.GREEN + message);
	}

	static String bugreport_folder = null;
	/**
	 * バグリポーター<br>
	 * Discordにメッセージ送ったり、管理部・モデレーターに通知したりする。
	 * @param exception
	 */
	public static void BugReporter(Throwable exception){
		// フォルダ作成
		if(bugreport_folder == null){
			String Path = JavaPlugin().getDataFolder() + File.separator + "bugreport" + File.separator;
			File folder = new File(Path);
			if(folder.exists()){
				bugreport_folder = Path;
			}else{
				if(folder.mkdir()){
					JavaPlugin().getLogger().info("BugReportのリポートディレクトリの作成に成功しました。");
					bugreport_folder = Path;
				}else{
					JavaPlugin().getLogger().info("BugReportのリポートディレクトリの作成に失敗しました。");
				}
			}
		}

		// ID作成
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		String id = sdf.format(new Date());

		String filepath = bugreport_folder + id + ".json";
		File file = new File(filepath);

		// バグ記録ファイル作成・記録
		try {
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			exception.printStackTrace(pw);
			fw.write(pw.toString());
			fw.close();
			JavaPlugin().getLogger().info("Bugreport: ファイル書き込みに成功");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			JavaPlugin().getLogger().info("Bugreport: ファイル書き込みに失敗");
			e.printStackTrace();
		}

		// 管理部・モデレーターにメッセージを送信
		for(Player p: Bukkit.getServer().getOnlinePlayers()) {
			String group = PermissionsManager.getPermissionMainGroup(p);
			if(group.equalsIgnoreCase("Admin") || group.equalsIgnoreCase("Moderator")) {
				p.sendMessage("[MyMaid] " + ChatColor.GREEN + "jaoProtectorのシステム障害が発生しました。");
				p.sendMessage("[MyMaid] " + ChatColor.GREEN + "エラー: " + exception.getMessage());
			}
		}

		// Discordへ送信
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		boolean res = Discord.send("293856671799967744", "jaoProtectorでエラーが発生しました。" + "\n"
				+ "```" + sw.toString() + "```\n"
				+ "Cause: `" + exception.getCause() + "`\n"
				+ "報告ID: `" + id + "`");
		if(res){
			JavaPlugin().getLogger().info("Bugreport: Discord送信に成功");
		}else{
			JavaPlugin().getLogger().info("Bugreport: Discord送信に失敗");
		}

		// スタックトレース出力とか
		JavaPlugin().getLogger().info("Bugreport: エラー発生。報告ID: 「" + id + "」");
		exception.printStackTrace();
	}
	public static JavaPlugin JavaPlugin(){
		if(JaoProtector.JavaPlugin == null){
			throw new NullPointerException("getJavaPlugin()が呼び出されましたが、jaoProtector.javapluginはnullでした。");
		}
		return JaoProtector.JavaPlugin;
	}
}
