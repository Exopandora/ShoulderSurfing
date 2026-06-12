package com.github.exopandora.shouldersurfing.event;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraCouplingEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraEntityTransparencyEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAttackStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerInteractionStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerPickStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerRideBoatStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerUseItemStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTargetCameraOffsetEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ForceVanillaPlayerInputEvent;
import com.github.exopandora.shouldersurfing.api.client.event.SetupCameraRotationEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraCouplingEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAimStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAttackStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerInteractionStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerPickStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerRideBoatStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerUseItemStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTargetCameraOffsetEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ForceVanillaPlayerInputEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.SetupCameraRotationEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;
import com.github.exopandora.shouldersurfing.api.event.Event;
import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.plugin.PluginContainer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus implements IEventBus {
	private final Map<Class<?>, HandlerList> handlerLists = new ConcurrentHashMap<Class<?>, HandlerList>();
	
	private boolean isFrozen;
	private PluginContainer activePluginContainer;
	
	private EventBus() {
		super();
	}
	
	@Override
	public void register(int priority, ComputePlayerAimStateEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputePlayerAimStateEvent.class);
	}
	
	@Override
	public void register(int priority, ComputeCameraCouplingEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputeCameraCouplingEvent.class);
	}
	
	@Override
	public void register(int priority, ComputeCameraEntityTransparencyEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputeCameraEntityTransparencyEvent.class);
	}
	
	@Override
	public void register(int priority, ComputePlayerAttackStateEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputePlayerAttackStateEvent.class);
	}
	
	@Override
	public void register(int priority, ComputePlayerInteractionStateEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputePlayerInteractionStateEvent.class);
	}
	
	@Override
	public void register(int priority, ComputePlayerPickStateEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputePlayerPickStateEvent.class);
	}
	
	@Override
	public void register(int priority, ComputePlayerRideBoatStateEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputePlayerRideBoatStateEvent.class);
	}
	
	@Override
	public void register(int priority, ComputePlayerUseItemStateEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputePlayerUseItemStateEvent.class);
	}
	
	@Override
	public void register(int priority, ComputeTargetCameraOffsetEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputeTargetCameraOffsetEvent.class);
	}
	
	@Override
	public void register(int priority, ForceVanillaPlayerInputEventHandler handler) {
		this.registerHandler(priority, handler::handle, ForceVanillaPlayerInputEvent.class);
	}
	
	@Override
	public void register(int priority, SetupCameraRotationEventHandler handler) {
		this.registerHandler(priority, handler::handle, SetupCameraRotationEvent.class);
	}
	
	@Override
	public void register(int priority, ComputeTemporaryFirstPersonStateEventHandler handler) {
		this.registerHandler(priority, handler::handle, ComputeTemporaryFirstPersonStateEvent.class);
	}
	
	@Override
	public void register(int priority, TickEventHandler handler) {
		this.registerHandler(priority, handler::handle, TickEvent.class);
	}
	
	@SuppressWarnings("unchecked")
	private  <T extends Event> void registerHandler(int priority, Consumer<T> consumer, Class<T> eventType) {
		this.checkState();
		HandlerList handlerList = this.handlerLists.computeIfAbsent(eventType, (type) ->
			new HandlerList(type.isAssignableFrom(CancellableEvent.class))
		);
		handlerList.add(priority, (Consumer<Event>) consumer, this.activePluginContainer);
	}
	
	private void checkState() {
		if (this.isFrozen) {
			throw new IllegalStateException("Unable to register event handlers outside loading stage");
		}
		if (this.activePluginContainer == null) {
			throw new IllegalStateException("No active plugin instance");
		}
	}
	
	void setActivePluginContainer(PluginContainer container) {
		this.activePluginContainer = container;
	}
	
	EventBus freeze() {
		this.isFrozen = true;
		this.activePluginContainer = null;
		return this;
	}
	
	public <T extends Event> T fire(T event) {
		HandlerList handlerList = this.handlerLists.get(event.getClass());
		if (handlerList == null) {
			return event;
		}
		for (EventHandler handler : handlerList.getListeners()) {
			try {
				handler.handle(event);
				if (handlerList.isEventCancellable() && ((CancellableEvent) event).isCancelled()) {
					return event;
				}
			} catch (Throwable t) {
				throw createException(handler.plugin(), t);
			}
		}
		return event;
	}
	
	public static EventBus create(List<PluginContainer> plugins) {
		EventBus eventBus = new EventBus();
		for (PluginContainer plugin : plugins) {
			try {
				eventBus.setActivePluginContainer(plugin);
				plugin.instance().register(eventBus);
			} catch (Throwable throwable) {
				ShoulderSurfingCommon.LOGGER.error(
					"Failed to register event handlers for plugin provided by {} ({})",
					plugin.modName(),
					plugin.modId(),
					throwable
				);
			}
		}
		return eventBus.freeze();
	}
	
	private static RuntimeException createException(PluginContainer plugin, Throwable throwable) {
		return new RuntimeException(
			"Shoulder Surfing Reloaded encountered an unexpected error while trying to execute an event handler provided by "
				+ plugin.modName()
				+ " ("
				+ plugin.modId()
				+ "). Please report this crash to the respective authors.",
			throwable
		);
	}
}
