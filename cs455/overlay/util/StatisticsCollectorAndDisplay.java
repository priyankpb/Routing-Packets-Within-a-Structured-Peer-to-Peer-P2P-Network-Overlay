package cs455.overlay.util;

import java.util.Map;

public class StatisticsCollectorAndDisplay {

	private int sum_packetSent = 0;
	private int sum_packetRelayed = 0;
	private long sum_payloadSent = 0;
	private int sum_packetReceived = 0;
	private long sum_payloadReceived = 0;

	public StatisticsCollectorAndDisplay(
			Map<Integer, TrafficSummary> trafficSummarycache) {

		System.out
				.println("--------------------------------------------------------------------------------");
		// System.out.println("NodeID\tSent\tReceived\tRelayed\tSum Values Sent\tSum Values Received");
		System.out.format("%9s%10s%10s%10s%20s%20s", "Node ID", "Sent",
				"Received", "Relayed", "Sent payload",
				"Received payload");
		System.out.println("");
		System.out
				.println("--------------------------------------------------------------------------------");

		for (Map.Entry<Integer, TrafficSummary> entrySet : trafficSummarycache
				.entrySet()) {
			Integer key = entrySet.getKey();
			TrafficSummary value = entrySet.getValue();

			// System.out.println(key + "\t" + value.packetSent + "\t" +
			// value.getPacketReceived() + "\t" + value.getPacketRelayed() +
			// "\t" + value.getPayloadSent() + "\t" +
			// value.getPayloadReceived());
			System.out.println("");
			System.out.format("%9s%10s%10s%10s%20s%20s", key,
					value.getPacketSent(), value.getPacketReceived(),
					value.getPacketRelayed(), value.getPayloadSent(),
					value.getPayloadReceived());
			sum_packetSent = sum_packetSent + value.getPacketSent();
			sum_packetReceived = sum_packetReceived + value.getPacketReceived();
			sum_packetRelayed = sum_packetRelayed + value.getPacketRelayed();
			sum_payloadSent = sum_payloadSent + value.getPayloadSent();
			sum_payloadReceived = sum_payloadReceived
					+ value.getPayloadReceived();

		}
		System.out.println("");
		System.out
				.println("--------------------------------------------------------------------------------");
		System.out.format("%9s%10s%10s%10s%20s%20s", "SUM", sum_packetSent,
				sum_packetReceived, sum_packetRelayed, sum_payloadSent,
				sum_payloadReceived);
	}

}
