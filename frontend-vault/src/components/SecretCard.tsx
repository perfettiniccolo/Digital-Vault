import { useState } from 'react';
import { Eye, EyeOff, Copy, Trash2, Edit2, Save, X, User, Key } from 'lucide-react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

export interface Secret {
    id: string;
    name: string;
    username: string;
    value: string;
    category: string;
    to_change: boolean;
}

interface SecretCardProps {
    secret: Secret;
    onSave?: (updatedSecret: Secret) => void;
    onDelete?: (id: string) => void;
}

export function SecretCard({ secret, onSave, onDelete }: SecretCardProps) {
    const [showPassword, setShowPassword] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState<Secret>(secret);
    // Stato per gestire il focus degli input (per l'effetto visivo)
    const [focusedInput, setFocusedInput] = useState<keyof Secret | null>(null);

    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
        isDragging
    } = useSortable({ id: secret.id });

    const dndStyle = {
        transform: CSS.Transform.toString(transform),
        transition: transition || 'transform 0.2s ease, box-shadow 0.2s ease', // Transizione fluida di default
        opacity: isDragging ? 0.5 : 1,
        zIndex: isDragging ? 999 : 1,
        cursor: isEditing ? 'default' : 'grab',
    };

    const handleChange = (field: keyof Secret, value: string) => {
        setFormData({ ...formData, [field]: value });
    };

    const handleSave = () => {
        if (onSave) onSave(formData);
        setIsEditing(false);
        setFocusedInput(null);
    };

    const handleCancel = () => {
        setFormData(secret);
        setIsEditing(false);
        setFocusedInput(null);
    };

    const bgStyle = secret.to_change
        ? {
            background: 'linear-gradient(145deg, rgba(69, 10, 10, 0.5) 0%, rgba(30, 41, 59, 0.5) 100%)',
            border: '1px solid rgba(248, 113, 113, 0.25)'
        }
        : {
            background: 'rgba(30, 41, 59, 0.6)', // Leggermente più opaco per contrasto in edit
            border: '1px solid rgba(255, 255, 255, 0.08)'
        };

    // --- NUOVO STILE PER GLI INPUT IN MODIFICA ---
    const getEditingInputStyle = (fieldName: keyof Secret) => ({
        flex: 1,
        padding: '12px 16px', // Più spazio interno
        borderRadius: '20px', // Curvatura molto morbida
        border: focusedInput === fieldName ? '1px solid rgba(96, 165, 250, 0.5)' : '1px solid rgba(255, 255, 255, 0.05)', // Bordo luminoso al focus
        background: focusedInput === fieldName ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.15)', // Sfondo leggermente più chiaro al focus
        color: '#F1F5F9', // Bianco morbido
        fontSize: '1rem',
        fontWeight: 400, // Testo più leggero
        outline: 'none',
        transition: 'all 0.3s ease', // Transizione fluida per focus
        boxShadow: focusedInput === fieldName ? '0 0 0 3px rgba(96, 165, 250, 0.15)' : 'none', // Alone luminoso al focus
        fontFamily: fieldName === 'value' ? 'monospace' : 'inherit', // Mantieni monospace per la password
        letterSpacing: fieldName === 'value' ? '1px' : 'normal',
    });

    return (
        <div
            ref={setNodeRef}
            style={{
                ...bgStyle,
                ...dndStyle,
                padding: '24px',
                borderRadius: '28px', // Curvatura della card ancora più accentuata
                boxShadow: isDragging ? '0 25px 50px -12px rgba(0, 0, 0, 0.6)' : '0 15px 35px -10px rgba(0, 0, 0, 0.4)', // Ombra più morbida
                display: 'flex',
                flexDirection: 'column',
                gap: '20px', // Più spazio tra gli elementi
                color: '#E2E8F0',
                backdropFilter: 'blur(16px)', // Sfocatura aumentata
                position: 'relative',
                touchAction: 'none',
                overflow: 'hidden', // Per contenere l'effetto vetro
            }}
            {...(isEditing ? {} : listeners)}
            {...(isEditing ? {} : attributes)}
        >
            {/* Header Card */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', minHeight: '40px' }}>
                {isEditing ? (
                    <input
                        type="text"
                        value={formData.name}
                        onChange={(e) => handleChange('name', e.target.value)}
                        onFocus={() => setFocusedInput('name')}
                        onBlur={() => setFocusedInput(null)}
                        style={getEditingInputStyle('name')}
                        placeholder="Nome del servizio"
                    />
                ) : (
                    <h3 style={{ margin: 0, fontSize: '1.3rem', fontWeight: '700', color: '#fff', letterSpacing: '-0.5px' }}>{secret.name}</h3>
                )}

                {!isEditing && (
                    <span style={{
                        fontSize: '0.75em',
                        color: '#7DD3FC',
                        backgroundColor: 'rgba(14, 165, 233, 0.15)',
                        padding: '6px 14px',
                        borderRadius: '20px',
                        fontWeight: '600',
                        letterSpacing: '0.5px',
                        boxShadow: '0 2px 6px rgba(0,0,0,0.1)'
                    }}>
                      {secret.category}
                    </span>
                )}
            </div>

            {/* Username */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', color: '#94A3B8', fontSize: '1rem' }}>
                <div style={{ padding: '8px', background: 'rgba(255,255,255,0.06)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <User size={16} />
                </div>
                {isEditing ? (
                    <input
                        type="text"
                        value={formData.username}
                        onChange={(e) => handleChange('username', e.target.value)}
                        onFocus={() => setFocusedInput('username')}
                        onBlur={() => setFocusedInput(null)}
                        style={getEditingInputStyle('username')}
                        placeholder="Username o Email"
                    />
                ) : (
                    <span style={{ fontWeight: 500, color: '#E2E8F0' }}>{secret.username}</span>
                )}
            </div>

            {/* Password Box */}
            <div style={isEditing ? {display: 'flex'} : {
                display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                backgroundColor: 'rgba(0, 0, 0, 0.25)',
                padding: '12px 18px',
                borderRadius: '20px',
                border: '1px solid rgba(255,255,255,0.06)'
            }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px', flex: 1 }}>
                    {!isEditing && <Key size={18} color="#64748B" />}
                    {isEditing ? (
                        <input
                            type="text"
                            value={formData.value}
                            onChange={(e) => handleChange('value', e.target.value)}
                            onFocus={() => setFocusedInput('value')}
                            onBlur={() => setFocusedInput(null)}
                            style={{...getEditingInputStyle('value'), width: '100%'}}
                            placeholder="Password"
                        />
                    ) : (
                        <span style={{ fontFamily: "monospace", color: '#F8FAFC', letterSpacing: '1.5px', fontSize: '1.05em' }}>
                            {showPassword ? secret.value : '••••••••••••'}
                        </span>
                    )}
                </div>

                {!isEditing && (
                    <div style={{ display: 'flex', gap: '6px' }}>
                        <button onClick={() => setShowPassword(!showPassword)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#94A3B8', padding: '6px', borderRadius: '50%', transition: 'background 0.2s, color 0.2s' }} className="hover:bg-white/5 hover:text-white">
                            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                        </button>
                        <button
                            onClick={() => navigator.clipboard.writeText(secret.value)}
                            style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#94A3B8', padding: '6px', borderRadius: '50%', transition: 'background 0.2s, color 0.2s' }}
                            className="hover:bg-white/5 hover:text-white"
                            title="Copia password"
                        >
                            <Copy size={18} />
                        </button>
                    </div>
                )}
            </div>

            {/* Toolbar */}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '10px' }}>
                {isEditing ? (
                    <>
                        <button onClick={handleCancel} style={{ cursor: 'pointer', background: 'rgba(255,255,255,0.08)', border: 'none', color: '#E2E8F0', padding: '10px', borderRadius: '50%', transition: 'all 0.2s ease', backdropFilter: 'blur(5px)' }} className="hover:bg-white/15 hover:scale-105">
                            <X size={20} />
                        </button>
                        <button onClick={handleSave} style={{ cursor: 'pointer', background: 'linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)', color: 'white', border: 'none', borderRadius: '50%', padding: '10px', boxShadow: '0 4px 15px rgba(59,130,246,0.4)', transition: 'all 0.2s ease' }} className="hover:scale-105 hover:shadow-lg">
                            <Save size={20} />
                        </button>
                    </>
                ) : (
                    <>
                        <button onClick={() => onDelete && onDelete(secret.id)} style={{ cursor: 'pointer', background: 'rgba(255,255,255,0.06)', border: 'none', color: '#94A3B8', borderRadius: '50%', padding: '12px', transition: 'all 0.2s ease', backdropFilter: 'blur(5px)' }} className="hover:bg-red-500/20 hover:text-red-400 hover:scale-105">
                            <Trash2 size={18} />
                        </button>
                        <button onClick={() => setIsEditing(true)} style={{ cursor: 'pointer', background: 'rgba(255,255,255,0.06)', border: 'none', color: '#F1F5F9', borderRadius: '50%', padding: '12px', transition: 'all 0.2s ease', backdropFilter: 'blur(5px)' }} className="hover:bg-white/15 hover:scale-105">
                            <Edit2 size={18} />
                        </button>
                    </>
                )}
            </div>
        </div>
    );
}