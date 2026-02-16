import { useState } from 'react';
import { Dices, Plus, Copy, Check } from 'lucide-react';
import { generatePassword } from '../utils/passwordGenerator';

interface HeaderProps {
    onAddSecret: () => void;
}

export function Header({ onAddSecret }: HeaderProps) {

    const [genPassword, setGenPassword] = useState<string | null>(null);
    const [isCopied, setIsCopied] = useState(false);

    const handleGenerate = () => {
        const newPass = generatePassword(12);
        setGenPassword(newPass);
        setIsCopied(false);
    };

    const handleCopy = () => {
        if (genPassword) {
            navigator.clipboard.writeText(genPassword);
            setIsCopied(true);
            setTimeout(() => {
                setGenPassword(null);
                setIsCopied(false);
            }, 2000);
        }
    };

    return (
        <header style={{
            display: 'flex', justifyContent: 'space-between', alignItems: 'center',
            padding: '20px 40px',
            borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
            backgroundColor: 'rgba(15, 23, 42, 0.6)', backdropFilter: 'blur(20px)',
            position: 'sticky', top: 0, zIndex: 100
        }}>

            {/* --- SINISTRA: Azioni --- */}
            <div style={{ display: 'flex', gap: '12px', position: 'relative', flex: 1 }}>
                <button
                    onClick={handleGenerate}
                    style={{
                        display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 20px',
                        background: 'linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)',
                        color: 'white', border: 'none', borderRadius: '50px',
                        cursor: 'pointer', fontWeight: '600', fontSize: '0.9em',
                        boxShadow: '0 4px 15px rgba(59, 130, 246, 0.4)',
                    }}
                >
                    <Dices size={18} />
                    <span>Generate</span>
                </button>

                {/* Popover Password */}
                {genPassword && (
                    <div style={{
                        position: 'absolute', top: '115%', left: 0,
                        background: 'rgba(30, 41, 59, 0.95)', border: '1px solid rgba(255,255,255,0.1)',
                        padding: '12px 16px', borderRadius: '12px',
                        boxShadow: '0 10px 25px -5px rgba(0,0,0,0.5)',
                        display: 'flex', alignItems: 'center', gap: '10px',
                        animation: 'fadeIn 0.2s ease-out', zIndex: 200, minWidth: '200px'
                    }}>
                        <span style={{ fontFamily: 'monospace', color: '#fff', fontSize: '1.1em' }}>{genPassword}</span>
                        <button onClick={handleCopy} style={{ background: isCopied ? '#22c55e' : 'rgba(255,255,255,0.1)', border: 'none', borderRadius: '6px', padding: '6px', cursor: 'pointer', color: 'white' }}>
                            {isCopied ? <Check size={16} /> : <Copy size={16} />}
                        </button>
                    </div>
                )}

                <button
                    onClick={onAddSecret}
                    style={{
                        display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 20px',
                        backgroundColor: 'rgba(255,255,255,0.05)', color: '#F1F5F9',
                        border: '1px solid rgba(255,255,255,0.1)', borderRadius: '50px',
                        cursor: 'pointer', fontWeight: '600', fontSize: '0.9em',
                        transition: 'background 0.2s'
                    }}
                >
                    <Plus size={18} />
                    <span>New</span>
                </button>
            </div>

            {/* --- CENTRO: Titolo --- */}
            <div style={{ flex: 2, textAlign: 'center' }}>
                <h1 style={{ margin: 0, fontSize: '1.8rem', fontWeight: '800', letterSpacing: '-0.5px', color: '#fff' }}>
                    DIGITAL VAULT
                </h1>
            </div>

            {/* --- DESTRA: Spazio vuoto per bilanciare il flex --- */}
            <div style={{ flex: 1 }}></div>

        </header>
    );
}