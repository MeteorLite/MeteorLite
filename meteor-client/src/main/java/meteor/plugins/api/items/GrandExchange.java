package meteor.plugins.api.items;

import meteor.plugins.api.commons.Time;
import meteor.plugins.api.entities.TileObjects;
import meteor.plugins.api.game.Game;
import meteor.plugins.api.game.GameThread;
import meteor.plugins.api.game.Vars;
import meteor.plugins.api.widgets.Dialog;
import meteor.plugins.api.widgets.Widgets;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GrandExchange {
	private static final int PRICE_VARBIT = 4398;
	private static final int QUANTITY_VARBIT = 4396;
	private static final Supplier<Widget> COLLECT_BUTTON = () -> Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 6, 0);
	private static final Supplier<Widget> CONFIRM_BUTTON = () -> Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 27);

	public static View getView() {
		Widget setupWindow = Widgets.get(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER);
		if (setupWindow != null && !GameThread.invokeLater(setupWindow::isHidden)) {
			String text = setupWindow.getChild(10).getText();
			if (text == null || text.isEmpty()) {
				return View.UNKNOWN;
			}

			if (text.equals("Sell offer")) {
				return View.SELLING;
			}

			if (text.equals("Buy offer")) {
				return View.BUYING;
			}

			// Widgets broke
			return View.UNKNOWN;
		}

		Widget geWindow = Widgets.get(WidgetInfo.GRAND_EXCHANGE_WINDOW_CONTAINER);
		if (geWindow != null && !GameThread.invokeLater(geWindow::isHidden)) {
			return View.OFFERS;
		}

		return View.CLOSED;
	}

	public static boolean isOpen() {
		return getView() != View.CLOSED && getView() != View.UNKNOWN;
	}

	public static boolean isSetupOpen() {
		return getView() == View.BUYING || getView() == View.SELLING;
	}

	public static void openBank() {
		TileObject bank = TileObjects.getNearest(x -> x.getName() != null
						&& x.getName().toLowerCase().contains("exchange booth") && x.hasAction("Bank"));
		if (bank != null) {
			bank.interact("Bank");
		}
	}

	public static boolean isSelling() {
		return getView() == View.SELLING;
	}

	public static boolean isBuying() {
		return getView() == View.BUYING;
	}

	public static int getItemId() {
		return Vars.getVarp(VarPlayer.CURRENT_GE_ITEM.getId());
	}

	public static void setItem(int id) {
		GameThread.invoke(() -> Game.getClient().runScript(754, id, 84));
	}

	public static int getPrice() {
		return Vars.getBit(PRICE_VARBIT);
	}

	public static void setPrice(int price) {
		Widget enterPriceButton = Widgets.get(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER);
		if (enterPriceButton != null && enterPriceButton.getChild(12) != null) {
			enterPriceButton.getChild(12).interact("Enter price");
			Dialog.enterInput(price);
		}
	}

	public static int getQuantity() {
		return Vars.getBit(QUANTITY_VARBIT);
	}

	public static void setQuantity(int quantity) {
		Widget enterPriceButton = Widgets.get(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER);
		if (enterPriceButton != null && enterPriceButton.getChild(7) != null) {
			enterPriceButton.getChild(7).interact("Enter quantity");
			Dialog.enterInput(quantity);
		}
	}

	public static int getGuidePrice() {
		Widget priceWidget = Widgets.get(WidgetInfo.GRAND_EXCHANGE_OFFER_PRICE);
		if (priceWidget != null) {
			return Integer.parseInt(priceWidget.getText().replaceAll("[^0-9]", ""));
		}

		return -1;
	}

	public static void open() {
		TileObject booth = TileObjects.getNearest(x -> x.hasAction("Exchange"));
		if (booth != null) {
			booth.interact("Exchange");
		}
	}

	public static void sell(Predicate<Item> filter) {
		Item item = Inventory.getFirst(filter);
		if (item != null) {
			Game.getClient().interact(1, 57, item.getSlot(), 30605312);
		}
	}

	public static void sell(int... ids) {
		sell(x -> {
			for (int id : ids) {
				if (id == x.getId()) {
					return true;
				}
			}

			return false;
		});
	}

	public static void sell(String... names) {
		sell(x -> {
			if (x.getName() == null) {
				return false;
			}

			for (String name : names) {
				if (name.equals(x.getName())) {
					return true;
				}
			}

			return false;
		});
	}

	public static void createBuyOffer() {
		List<Widget> geRoot = Widgets.get(465);

		if (getEmptySlots() <= 0) {
			return;
		}

		if (geRoot == null) {
			return;
		}

		for (int i = 7; i < 14; i++) {
			Widget box = geRoot.get(i);
			if (box == null) {
				continue;
			}

			Widget buyButton = box.getChild(3);
			if (buyButton == null || GameThread.invokeLater(buyButton::isHidden)) {
				continue;
			}

			buyButton.interact(0);
		}
	}

	public static void abortOffer(int itemId) {
		List<Widget> geRoot = Widgets.get(465);

		if (getEmptySlots() == 0) {
			return;
		}

		if (geRoot == null) {
			return;
		}

		for (int i = 7; i < 14; i++) {
			Widget box = geRoot.get(i);
			if (box == null) {
				continue;
			}

			if (box.getActions() == null || !box.hasAction("Abort offer")) {
				continue;
			}

			Widget itemBox = box.getChild(18);
			if (itemBox == null || GameThread.invokeLater(itemBox::isHidden)) {
				continue;
			}

			if (itemBox.getItemId() == itemId) {
				itemBox.interact("Abort offer");
				return;
			}
		}
	}

	public static int getEmptySlots() {
		return Game.getMembershipDays() <= 0 ? 3 - getOffers().size() : 8 - getOffers().size();
	}

	public static List<GrandExchangeOffer> getOffers() {
		List<GrandExchangeOffer> out = new ArrayList<>();
		GrandExchangeOffer[] offers = Game.getClient().getGrandExchangeOffers();
		if (offers != null) {
			for (GrandExchangeOffer offer : offers) {
				if (offer.getItemId() > 0) {
					out.add(offer);
				}
			}
		}

		return out;
	}

	public static boolean canCollect() {
		Widget collect = COLLECT_BUTTON.get();
		return collect != null && !GameThread.invokeLater(collect::isHidden);
	}

	public static void collect() {
		collect(false);
	}

	public static void collect(boolean toBank) {
		Widget collect = COLLECT_BUTTON.get();
		if (collect != null && !GameThread.invokeLater(collect::isHidden)) {
			collect.interact(toBank ? "Collect to bank" : "Collect to inventory");
		}
	}

	public static void confirm() {
		Widget confirm = CONFIRM_BUTTON.get();
		if (confirm != null && !GameThread.invokeLater(confirm::isHidden)) {
			confirm.interact("Confirm");
		}
	}

	public static boolean isSearchingItem() {
		return Vars.getVarcInt(VarClientInt.INPUT_TYPE) == 14;
	}

	public static void openItemSearch() {
		Game.getClient().interact(1, 57, 0, 30474264);
	}

	public static boolean sell(int itemId, int quantity, int price) {
		return exchange(false, itemId, quantity, price, true, false);
	}

	public static boolean sell(int itemId, int quantity, int price, boolean collect, boolean toBank) {
		return exchange(false, itemId, quantity, price, collect, toBank);
	}

	public static boolean buy(int itemId, int quantity, int price) {
		return exchange(true, itemId, quantity, price, true, false);
	}

	public static boolean buy(int itemId, int quantity, int price, boolean collect, boolean toBank) {
		return exchange(true, itemId, quantity, price, collect, toBank);
	}

	public static boolean exchange(boolean buy, int itemId, int quantity, int price) {
		return exchange(buy, itemId, quantity, price, true, false);
	}

	public static boolean exchange(boolean buy, int itemId, int quantity, int price, boolean collect, boolean toBank) {
		if (!isOpen()) {
			open();
			return false;
		}

		if (collect && canCollect()) {
			collect(toBank);
			return false;
		}

		if (buy) {
			if (!isBuying()) {
				createBuyOffer();
				return false;
			}
		} else {
			if (!isSelling()) {
				sell(itemId);
				return false;
			}
		}

		if (getItemId() != itemId) {
			if (buy) {
				if (!isSearchingItem()) {
					openItemSearch();
				}

				setItem(itemId);
			} else {
				sell(itemId);
			}

			return false;
		}

		if (getItemId() == itemId) {
			if (getPrice() != price) {
				setPrice(price);
			}

			if (getQuantity() != quantity) {
				setQuantity(quantity);
			}

			Time.sleepUntil(() -> getPrice() == price && getQuantity() == quantity, 3000);

			if (getPrice() == price && getQuantity() == quantity) {
				confirm();
				return true;
			}
		}

		return false;
	}

	public enum View {
		CLOSED, OFFERS, BUYING, SELLING, UNKNOWN
	}
}