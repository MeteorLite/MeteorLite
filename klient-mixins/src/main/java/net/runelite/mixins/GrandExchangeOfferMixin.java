package net.runelite.mixins;

import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.rs.api.RSGrandExchangeOffer;

import static net.runelite.api.GrandExchangeOfferState.*;

@Mixin(RSGrandExchangeOffer.class)
public abstract class GrandExchangeOfferMixin implements RSGrandExchangeOffer {

	/*
		 Internally a GrandExchangeOffer's state is represented as 4 flags
		 packed into the lower half of a byte. They are:
	*/

	//Set for sell offers, unset for buy offers
	private static final int IS_SELLING = 1 << 3; // 0b1000


	/*
	Set for offers that have finished, either because they've
	been filled, or because they were cancelled
	*/
	private static final int COMPLETED = 1 << 2; // 0b0100

	/*
	Set for offers that are actually live
	NB: Insta-buy/sell offers will be simultaneously LIVE and LOCAL
	*/
	private static final int LIVE = 1 << 1; // 0b0010

	//True for just-made, just-cancelled, completely cancelled, and completed offers
	private static final int LOCAL = 1;

	@Inject
	@Override
	public GrandExchangeOfferState getState()
	{
		byte code = getRSState();
		boolean isSelling = (code & IS_SELLING) == IS_SELLING;
		boolean isFinished = (code & COMPLETED) == COMPLETED;


		if (code == 0)
		{
			return EMPTY;
		}
		else if (isFinished && getQuantitySold() < getTotalQuantity())
		{
			return isSelling ? CANCELLED_SELL : CANCELLED_BUY;
		}
		else if (isSelling)
		{
			if (isFinished)
			{
				return SOLD;
			}
			else // if isUnfinished
			{
				return SELLING;
			}
		}
		else // if isBuying
		{
			if (isFinished)
			{
				return BOUGHT;
			}
			else // if isUnfinished
			{
				return BUYING;
			}
		}
	}
}
