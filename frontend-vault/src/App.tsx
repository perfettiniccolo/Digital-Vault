import { useState } from 'react';
import './App.css';
import { SecretCard, type Secret } from './components/SecretCard';
import { Header } from './components/Header';
import { SecretModal } from './components/SecretModal';
import {Search, AlertTriangle, ChevronDown} from 'lucide-react';

// --- IMPORT PER IL DRAG & DROP ---
import {
    DndContext,
    closestCenter,
    KeyboardSensor,
    PointerSensor,
    useSensor,
    useSensors,
    type DragEndEvent,
} from '@dnd-kit/core';
import {
    arrayMove,
    SortableContext,
    sortableKeyboardCoordinates,
    rectSortingStrategy,
} from '@dnd-kit/sortable';

function App() {
    const [secrets, setSecrets] = useState<Secret[]>([
        { id: "1", name: "Instagram", username: "niccolo_perfetti", value: "SuperSecret1!", category: "Social", to_change: false },
        { id: "2", name: "Banca Intesa", username: "niccolo88", value: "Codice1234", category: "Finanza", to_change: true },
        { id: "3", name: "Gmail Lavoro", username: "n.perfetti@azienda.com", value: "WorkPass2024", category: "Lavoro", to_change: false }
    ]);

    const [searchTerm, setSearchTerm] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("All");
    const [showOnlyToChange, setShowOnlyToChange] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // --- CONFIGURAZIONE SENSORI ---
    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: {
                distance: 8, // Evita che il click sui bottoni venga scambiato per un inizio di trascinamento
            },
        }),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    const handleOpenModal = () => setIsModalOpen(true);

    const handleSaveNewSecret = (newSecret: Secret) => {
        setSecrets([newSecret, ...secrets]);
        setIsModalOpen(false);
    };

    const handleUpdateSecret = (updatedSecret: Secret) => {
        setSecrets(secrets.map(s => s.id === updatedSecret.id ? updatedSecret : s));
    };

    const handleDeleteSecret = (idToDelete: string) => {
        if (!window.confirm("Sei sicuro?")) return;
        setSecrets(secrets.filter(s => s.id !== idToDelete));
    };

    // --- LOGICA DI FINE TRASCINAMENTO ---
    const handleDragEnd = (event: DragEndEvent) => {
        const { active, over } = event;

        if (over && active.id !== over.id) {
            setSecrets((items) => {
                const oldIndex = items.findIndex((i) => i.id === active.id);
                const newIndex = items.findIndex((i) => i.id === over.id);
                return arrayMove(items, oldIndex, newIndex);
            });
        }
    };

    const filteredSecrets = secrets.filter(secret => {
        const term = searchTerm.toLowerCase();
        const matchesSearch = secret.name.toLowerCase().includes(term) ||
            secret.username.toLowerCase().includes(term);
        const matchesCategory = selectedCategory === "All" || secret.category === selectedCategory;
        const matchesToChange = !showOnlyToChange || secret.to_change;
        return matchesSearch && matchesCategory && matchesToChange;
    });

    return (
        <div style={{
            fontFamily: "'Inter', sans-serif", minHeight: '100vh',
            background: 'radial-gradient(circle at top center, #1e293b 0%, #0f172a 100%)',
            color: '#F1F5F9'
        }}>
            <Header onAddSecret={handleOpenModal} />

            <div style={{ padding: '20px 20px 40px 20px', maxWidth: '1200px', margin: '0 auto' }}>

                {/* Barra di ricerca centrale */}
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '15px', marginBottom: '40px', flexWrap: 'wrap' }}>
                    <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                        <Search size={20} style={{ position: 'absolute', left: '16px', color: '#94A3B8' }} />
                        <input
                            type="text"
                            placeholder="Cerca segreti..."
                            onChange={(e) => setSearchTerm(e.target.value)}
                            style={{
                                padding: '14px 16px 14px 50px', borderRadius: '50px',
                                border: '1px solid rgba(255,255,255,0.1)', backgroundColor: 'rgba(30, 41, 59, 0.7)',
                                color: '#F1F5F9', outline: 'none', width: '300px', fontSize: '1rem',
                                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
                            }}
                        />
                    </div>

                    {/* MENU A TENDINA STILIZZATO */}
                    <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                        <select
                            onChange={(e) => setSelectedCategory(e.target.value)}
                            style={{
                                appearance: 'none', // 🪄 Rimuove lo stile di default del browser
                                padding: '14px 40px 14px 24px', // Spazio a destra per la freccia
                                borderRadius: '50px',
                                border: '1px solid rgba(255,255,255,0.1)',
                                backgroundColor: 'rgba(30, 41, 59, 0.7)',
                                color: '#F1F5F9',
                                cursor: 'pointer',
                                outline: 'none',
                                fontSize: '1rem',
                                minWidth: '160px',
                                backdropFilter: 'blur(10px)',
                                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
                            }}
                        >
                            <option value="All">Tutte le Categorie</option>
                            <option value="Social">Social </option>
                            <option value="Finanza">Finanza </option>
                            <option value="Lavoro">Lavoro </option>
                            <option value="Altro">Altro </option>
                        </select>

                        {/* Icona della freccia personalizzata */}
                        <ChevronDown
                            size={18}
                            style={{
                                position: 'absolute',
                                right: '18px',
                                color: '#94A3B8',
                                pointerEvents: 'none' // 👈 Importante: i click devono passare "attraverso" l'icona
                            }}
                        />
                    </div>

                    <button
                        onClick={() => setShowOnlyToChange(!showOnlyToChange)}
                        style={{
                            display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px',
                            padding: '12px 24px', borderRadius: '50px',
                            border: showOnlyToChange ? '1px solid #EF4444' : '1px solid rgba(255,255,255,0.1)',
                            backgroundColor: showOnlyToChange ? 'rgba(239, 68, 68, 0.15)' : 'rgba(30, 41, 59, 0.7)',
                            color: showOnlyToChange ? '#EF4444' : '#94A3B8',
                            cursor: 'pointer', transition: 'all 0.3s ease',
                            boxShadow: showOnlyToChange ? '0 0 15px rgba(239, 68, 68, 0.3)' : '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
                            fontWeight: 500, fontSize: '0.95em', height: '48px'
                        }}
                    >
                        <AlertTriangle size={18} />
                        <span>Da cambiare</span>
                    </button>
                </div>

                {/* --- GRIGLIA CON DRAG & DROP --- */}
                <DndContext
                    sensors={sensors}
                    collisionDetection={closestCenter}
                    onDragEnd={handleDragEnd}
                >
                    <SortableContext
                        items={filteredSecrets.map(s => s.id)}
                        strategy={rectSortingStrategy}
                    >
                        <div style={{
                            display: 'grid', gap: '25px',
                            gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))'
                        }}>
                            {filteredSecrets.length === 0 && (
                                <div style={{ gridColumn: '1/-1', textAlign: 'center', color: '#64748B', marginTop: '20px' }}>
                                    <p>Nessun segreto trovato.</p>
                                </div>
                            )}

                            {filteredSecrets.map((secret) => (
                                <SecretCard
                                    key={secret.id}
                                    secret={secret}
                                    onSave={handleUpdateSecret}
                                    onDelete={handleDeleteSecret}
                                />
                            ))}
                        </div>
                    </SortableContext>
                </DndContext>
            </div>

            {isModalOpen && (
                <SecretModal onSave={handleSaveNewSecret} onClose={() => setIsModalOpen(false)} />
            )}
        </div>
    )
}

export default App;