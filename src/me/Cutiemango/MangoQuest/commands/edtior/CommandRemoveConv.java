package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestConfigManager;

public class CommandRemoveConv
{
	// /mq ce remove args[2] args[3]
	public static void execute(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length >= 3)
		{
			switch (args[2])
			{
				case "conv":
					removeConv(conv, sender, args);
					return;
				case "confirm":
					removeConfirm(conv, sender, args);
					return;
			}
		}
		if (!QuestEditorManager.checkEditorMode(sender, true))
			return;
		switch (args[2])
		{
			case "act":
			case "acceptact":
			case "denyact":
				removeAction(conv, sender, args);
				break;
		}
		return;
	}

	private static void removeConfirm(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length == 4)
		{
			if (ConversationManager.getConversation(args[3]) != null)
			{
				QuestConversation target = ConversationManager.getConversation(args[3]);
				ConversationEditorManager.removeConfirmGUI(sender, target);
				return;
			}
		}
	}

	private static void removeConv(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length == 4)
		{
			if (ConversationManager.getConversation(args[3]) != null)
			{
				QuestConversation target = ConversationManager.getConversation(args[3]);
				for (Player pl : Bukkit.getOnlinePlayers())
				{
					if (ConversationManager.isInConvProgress(pl, target))
						ConversationManager.forceQuit(pl, conv);
				}
				QuestConfigManager.getSaver().removeConversation(conv);
				QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConversationRemoved", target.getName()));
				QuestStorage.Conversations.remove(target.getInternalID());
				ConversationEditorManager.removeGUI(sender);
				return;
			}
		}
	}

	// /mq ce remove [acceptact/denyact/act] [index]
	private static void removeAction(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length < 4)
			return;
		int index = Integer.parseInt(args[3]);
		switch (args[2])
		{
			case "act":
				List<QuestBaseAction> list = conv.getActions();
				list.remove(index);
				conv.setActions(list);
				break;
			case "acceptact":
				list = ((StartTriggerConversation) conv).getAcceptActions();
				list.remove(index);
				((StartTriggerConversation) conv).setAcceptActions(list);
				break;
			case "denyact":
				list = ((StartTriggerConversation) conv).getDenyActions();
				list.remove(index);
				((StartTriggerConversation) conv).setDenyActions(list);
				break;
		}
		ConversationEditorManager.editConversation(sender);
		return;
	}
}
