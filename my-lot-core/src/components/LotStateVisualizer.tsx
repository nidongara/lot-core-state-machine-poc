"use client";

import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { Dialog, DialogContent, DialogTrigger } from "@radix-ui/react-dialog";
import { Button } from "@/components/ui/Button";
import Graphviz from "graphviz-react";

const API_BASE_URL = "http://localhost:8080/lot"; // Update with backend URL

// Define LotState type
type LotState =
    | "NEW"
    | "WAITING_FOR_CLEAR_CHARGES"
    | "WAITING_FOR_CLEAR_FOR_PICKUP"
    | "WAITING_FOR_DISPATCH"
    | "WAITING_FOR_INVENTORY";

// Define LotEvent type
type LotEvent = "CLEAR_CHARGES" | "CLEAR_FOR_PICKUP" | "DISPATCH" | "MOVE_TO_INVENTORY";

// Define Lot type
interface Lot {
    id: string;
    state: LotState;
}

// State transitions
const eventTransitions: Record<LotState, { event: LotEvent; target: LotState }[]> = {
    NEW: [
        { event: "CLEAR_CHARGES", target: "WAITING_FOR_CLEAR_CHARGES" },
        { event: "MOVE_TO_INVENTORY", target: "WAITING_FOR_INVENTORY" },
    ],
    WAITING_FOR_CLEAR_CHARGES: [{ event: "CLEAR_FOR_PICKUP", target: "WAITING_FOR_CLEAR_FOR_PICKUP" }],
    WAITING_FOR_CLEAR_FOR_PICKUP: [{ event: "DISPATCH", target: "WAITING_FOR_DISPATCH" }],
    WAITING_FOR_DISPATCH: [],
    WAITING_FOR_INVENTORY: [],
};

const LotGraph: React.FC = () => {
    const [lot, setLot] = useState<Lot | null>(null);
    const [lotId, setLotId] = useState("4345346");
    const [error, setError] = useState("");
    const [dot, setDot] = useState("");

    // Fetch Lot state from API
    const fetchLotState = useCallback(async () => {
        setError("");
        try {
            const response = await axios.get(`${API_BASE_URL}/${lotId}/state`);
            setLot({ id: lotId, state: response.data });
        } catch (err) {
            setError("Error fetching lot state.");
        }
    }, [lotId]);

    // Fetch Lot state from API
    const fetchLotGraphViz = useCallback(async () => {
        setError("");
        try {
            const response = await axios.get(`${API_BASE_URL}/graphviz/${lotId}`);
            setDot(response.data);
        } catch (err) {
            setError("Error fetching lot GraphViz.");
        }
    }, [lotId]);

    // Handle event transition
    const triggerEvent = async (event: LotEvent) => {
        setError("");
        try {
            const response = await axios.post(`${API_BASE_URL}/${lotId}/event/${event}`);
            setLot({ id: lotId, state: response.data });
        } catch (err) {
            setError("Failed to update state.");
        }
    };


    useEffect(() => {
        fetchLotState();
        fetchLotGraphViz();
    }, [fetchLotState]);

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Lot State Visualizer (Graphviz)</h1>

            <div className="mb-4">
                <input
                    type="text"
                    className="border p-2 rounded"
                    value={lotId}
                    onChange={(e) => setLotId(e.target.value)}
                    placeholder="Enter Lot ID"
                />
                <Button onClick={fetchLotState} className="ml-2">Load Lot</Button>
            </div>

            {error && <p className="text-red-500">{error}</p>}

            {dot &&
                <Graphviz dot={dot} />
           }

            {lot && (
                <Dialog>
                    <DialogTrigger asChild>
                        <Button className="mt-4">Trigger Event</Button>
                    </DialogTrigger>
                    <DialogContent className="p-4 bg-white rounded-lg">
                        <h2 className="text-xl font-bold mb-2">Trigger Event for Lot {lot.id}</h2>
                        {eventTransitions[lot.state].map(({ event }) => (
                            <Button key={event} onClick={() => triggerEvent(event)} className="block mt-2">
                                {event}
                            </Button>
                        ))}
                    </DialogContent>
                </Dialog>
            )}
        </div>
    );
};

export default LotGraph;
