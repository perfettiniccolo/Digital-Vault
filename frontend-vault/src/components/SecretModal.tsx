import { useState } from 'react';
import { X, Save } from 'lucide-react';
import type {Secret} from './SecretCard';

interface SecretModalProps {
    onSave: (newSecret: Secret) => void;
    onClose: () => void;
}

export function SecretModal({ onSave, onClose }: SecretModalProps) {
    // Stato temporaneo del form
    const [formData, setFormData] = useState<Secret>({
        // eslint-disable-next-line react-hooks/purity
        id: Date.now().toString(), // ID temporaneo
        name: '',
        username: '',
        value: '',
        category: 'Altro',
        to_change: false
    });

    const handleChange = (field: keyof Secret, value: string) => {
        setFormData({ ...formData, [field]: value });
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault(); // Evita il ricaricamento della pagina
        // Validazione base: se manca il nome o la password, non salviamo
        if (!formData.name || !formData.value) {
            alert("Per favore inserisci almeno Nome e Password!");
            return;
        }
        onSave(formData);
    };

    return (
        // OVERLAY: Lo sfondo scuro che copre tutta la pagina
        <div style={{
            position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
            backgroundColor: 'rgba(15, 23, 42, 0.8)', // Sfondo scuro semi-trasparente
            backdropFilter: 'blur(5px)',
            display: 'flex', justifyContent: 'center', alignItems: 'center',
            zIndex: 1000
        }}>

            {/* IL MODALE VERO E PROPRIO */}
            <div style={{
                backgroundColor: '#1E293B', padding: '30px', borderRadius: '24px',
                width: '100%', maxWidth: '500px',
                boxShadow: '0 20px 50px rgba(0,0,0,0.5)',
                border: '1px solid rgba(255,255,255,0.1)',
                color: '#F1F5F9'
            }}>

                {/* Intestazione */}
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                    <h2 style={{ margin: 0 }}>Nuovo Segreto ✨</h2>
                    <button onClick={onClose} style={{ background: 'none', border: 'none', color: '#94A3B8', cursor: 'pointer' }}>
                        <X size={24} />
                    </button>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                    <div>
                        <label style={{ display: 'block', marginBottom: '5px', fontSize: '0.9em', color: '#94A3B8' }}>Nome Servizio</label>
                        <input
                            autoFocus
                            type="text" placeholder="Es. Netflix, Amazon..."
                            value={formData.name} onChange={e => handleChange('name', e.target.value)}
                            style={inputStyle}
                        />
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '5px', fontSize: '0.9em', color: '#94A3B8' }}>Username / Email</label>
                        <input
                            type="text" placeholder="user@example.com"
                            value={formData.username} onChange={e => handleChange('username', e.target.value)}
                            style={inputStyle}
                        />
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '5px', fontSize: '0.9em', color: '#94A3B8' }}>Password</label>
                        <input
                            type="text" placeholder="Password sicura"
                            value={formData.value} onChange={e => handleChange('value', e.target.value)}
                            style={{ ...inputStyle, fontFamily: 'monospace' }}
                        />
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '5px', fontSize: '0.9em', color: '#94A3B8' }}>Categoria</label>
                        <select
                            value={formData.category} onChange={e => handleChange('category', e.target.value)}
                            style={inputStyle}
                        >
                            <option value="Social">Social</option>
                            <option value="Finanza">Finanza</option>
                            <option value="Lavoro">Lavoro</option>
                            <option value="Altro">Altro</option>
                        </select>
                    </div>

                    {/* Footer con Bottoni */}
                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
                        <button type="button" onClick={onClose} style={{ padding: '10px 20px', borderRadius: '12px', border: 'none', background: 'transparent', color: '#94A3B8', cursor: 'pointer' }}>
                            Annulla
                        </button>
                        <button type="submit" style={{
                            padding: '10px 24px', borderRadius: '50px', border: 'none',
                            background: '#3B82F6', color: 'white', fontWeight: 'bold',
                            cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px'
                        }}>
                            <Save size={18} /> Salva Segreto
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
}

// Stile condiviso per gli input per tenere il codice pulito
const inputStyle = {
    width: '100%', padding: '12px', borderRadius: '12px',
    border: '1px solid rgba(255,255,255,0.1)',
    backgroundColor: 'rgba(15, 23, 42, 0.5)',
    color: 'white', outline: 'none', fontSize: '1em'
};